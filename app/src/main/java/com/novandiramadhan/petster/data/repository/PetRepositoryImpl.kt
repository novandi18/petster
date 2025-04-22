package com.novandiramadhan.petster.data.repository

import android.content.Context
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.FirebaseKeys
import com.novandiramadhan.petster.common.states.PetFilterState
import com.novandiramadhan.petster.data.paging.PetPagingSource
import com.novandiramadhan.petster.data.paging.VolunteerPetPagingSource
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.domain.model.PetHome
import com.novandiramadhan.petster.domain.model.PetResult
import com.novandiramadhan.petster.domain.model.PetView
import com.novandiramadhan.petster.domain.model.Result
import com.novandiramadhan.petster.domain.model.ShelterLocation
import com.novandiramadhan.petster.domain.model.VolunteerDashboardResult
import com.novandiramadhan.petster.domain.repository.PetRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

class PetRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val firestore: FirebaseFirestore
): PetRepository {
    override fun addPet(pet: Pet): Flow<Resource<PetResult>> {
        return flow {
            emit(Resource.Loading())
            val documentRef = firestore.collection(FirebaseKeys.PET_COLLECTION).document()
            val petWithId = pet.copy(
                id = documentRef.id,
                createdAt = Timestamp.now(),
            )
            documentRef.set(petWithId).await()

            emit(Resource.Success(PetResult("Pet added successfully", petWithId)))
        }.catch { e ->
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override fun deletePet(petId: String): Flow<Resource<Result>> {
        return flow {
            emit(Resource.Loading())

            try {
                if (petId.isBlank()) {
                    emit(Resource.Error(
                        message = "Pet ID cannot be empty",
                        messageResId = R.string.error_empty_pet_id
                    ))
                    return@flow
                }

                firestore.collection(FirebaseKeys.PET_COLLECTION).document(petId).delete().await()

                val favoritedQuery = firestore.collection(FirebaseKeys.FAVORITED_PET_COLLECTION)
                    .whereEqualTo("petId", petId)
                    .get()
                    .await()

                for (document in favoritedQuery.documents) {
                    document.reference.delete().await()
                }

                emit(Resource.Success(Result(context.getString(R.string.pet_delete_success))))
            } catch (e: Exception) {
                val errorResource: Resource.Error<Result> = when (e) {
                    is FirebaseFirestoreException -> {
                        if (e.cause is IOException) {
                            Resource.Error(
                                message = e.message.toString(),
                                messageResId = R.string.internet_error
                            )
                        } else {
                            Resource.Error(
                                message = e.message.toString(),
                                messageResId = R.string.pet_delete_error_internal
                            )
                        }
                    }
                    else -> {
                        Resource.Error(
                            message = e.message ?: "Unknown error occurred",
                            messageResId = R.string.pet_delete_error
                        )
                    }
                }
                emit(errorResource)
            }
        }.catch { e ->
            val errorResource: Resource.Error<Result> = when (e) {
                is IOException -> Resource.Error(
                    message = e.message.toString(),
                    messageResId = R.string.internet_error
                )
                else -> Resource.Error(
                    message = e.message ?: "Unknown error occurred",
                    messageResId = R.string.error_unknown
                )
            }
            emit(errorResource)
        }
    }

    override fun getPetsHome(limitEachCategory: Int, shelterId: String): Flow<Resource<PetHome>> {
        return flow {
            emit(Resource.Loading<PetHome>())
            try {
                val categories = context.resources.getStringArray(R.array.pet_categories).toList()
                val dogPets = mutableListOf<Pet>()
                val catPets = mutableListOf<Pet>()
                val otherPets = mutableListOf<Pet>()

                val favoritePetIds = try {
                    val favoritesSnapshot = firestore.collection(FirebaseKeys.FAVORITED_PET_COLLECTION)
                        .whereEqualTo("shelterId", shelterId)
                        .get()
                        .await()
                    val ids = favoritesSnapshot.documents.mapNotNull { it.getString("petId") }.toSet()
                    Log.d("PetRepositoryImpl", "Favorite pet IDs found: $ids")
                    ids
                } catch (e: Exception) {
                    Log.e("PetRepositoryImpl", "Error fetching favorites", e)
                    emptySet<String>()
                }

                for (category in categories) {
                    val query = firestore.collection(FirebaseKeys.PET_COLLECTION)
                        .whereEqualTo("category", category)
                        .whereEqualTo("adopted", false)
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .limit(limitEachCategory.toLong())

                    val petsSnapshot = query.get().await()
                    Log.d("PetRepositoryImpl", "Firestore query for '$category' returned ${petsSnapshot.size()} documents.")

                    val categoryPets = petsSnapshot.documents.mapNotNull { document ->
                        document.toObject(Pet::class.java)?.copy(id = document.id)
                    }.map { pet ->
                        if (pet.id != null && pet.id in favoritePetIds) {
                            pet.copy(isFavorite = true)
                        } else {
                            pet
                        }
                    }

                    when (category) {
                        "Dog" -> dogPets.addAll(categoryPets)
                        "Cat" -> catPets.addAll(categoryPets)
                        "Other" -> otherPets.addAll(categoryPets)
                    }
                }

                val petHomeResult = PetHome(
                    dog = dogPets,
                    cat = catPets,
                    other = otherPets
                )
                Log.d("PetRepositoryImpl", petHomeResult.toString())

                emit(Resource.Success(petHomeResult))

            } catch (e: Exception) {
                val errorResource: Resource.Error<PetHome> = when (e) {
                    is FirebaseFirestoreException -> {
                        Resource.Error(
                            messageResId = R.string.pet_error_internal,
                            message = e.message.toString()
                        )
                    }
                    else -> {
                        Resource.Error(
                            messageResId = R.string.error_unknown,
                            message = e.message.toString()
                        )
                    }
                }
                emit(errorResource)
            }
        }
    }

    override fun getPets(
        shelterId: String?,
        filter: PetFilterState?,
        shelterLocation: ShelterLocation?
    ): Flow<PagingData<Pet>> {
        val pagingConfig = PagingConfig(
            pageSize = 10,
            prefetchDistance = 5,
            enablePlaceholders = false,
            initialLoadSize = 10
        )

        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                PetPagingSource(
                    firestore = firestore,
                    shelterId = shelterId,
                    filter = filter,
                    shelterLocation = shelterLocation
                )
            }
        ).flow
    }

    override fun getPetById(id: String, shelterId: String?): Flow<Resource<Pet>> {
        return flow {
            emit(Resource.Loading())

            try {
                val petDoc = firestore.collection(FirebaseKeys.PET_COLLECTION)
                    .document(id)
                    .get()
                    .await()

                if (petDoc.exists()) {
                    var pet = petDoc.toObject(Pet::class.java)?.copy(id = petDoc.id)

                    if (pet != null && shelterId != null) {
                        val favoriteQuery = firestore.collection(FirebaseKeys.FAVORITED_PET_COLLECTION)
                            .whereEqualTo("petId", id)
                            .whereEqualTo("shelterId", shelterId)
                            .limit(1)
                            .get()
                            .await()

                        pet = pet.copy(isFavorite = !favoriteQuery.isEmpty)
                    }

                    if (pet != null) {
                        emit(Resource.Success(pet))
                    } else {
                        emit(Resource.Error("Pet data couldn't be converted"))
                    }
                } else {
                    emit(Resource.Error("Pet not found"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Error fetching pet"))
            }
        }.catch { e ->
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override fun addViewedPet(petView: PetView): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.Loading())

            try {
                if (petView.petId.isNullOrBlank() || petView.shelterId.isNullOrBlank()) {
                    emit(Resource.Error("Pet ID and Shelter ID cannot be empty"))
                    return@flow
                }

                val timestamp = Timestamp.now()
                val existingViewQuery = firestore.collection(FirebaseKeys.PET_VIEWS_COLLECTION)
                    .whereEqualTo("petId", petView.petId)
                    .whereEqualTo("shelterId", petView.shelterId)
                    .limit(1)
                    .get()
                    .await()

                if (existingViewQuery.documents.isNotEmpty()) {
                    val existingDoc = existingViewQuery.documents[0]
                    existingDoc.reference.update("timestamp", timestamp).await()
                } else {
                    val viewWithTimestamp = petView.copy(timestamp = timestamp)
                    firestore.collection(FirebaseKeys.PET_VIEWS_COLLECTION)
                        .document()
                        .set(viewWithTimestamp)
                        .await()
                }

                emit(Resource.Success(Unit))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to record pet view"))
            }
        }.catch { e ->
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override fun getVolunteerDashboard(volunteerId: String): Flow<Resource<VolunteerDashboardResult>> {
        return flow {
            emit(Resource.Loading())

            try {
                if (volunteerId.isBlank()) {
                    emit(Resource.Error(
                        message = "Volunteer ID cannot be empty",
                        messageResId = R.string.error_empty_volunteer_id
                    ))
                    return@flow
                }

                val volunteerPath = "${FirebaseKeys.VOLUNTEER_COLLECTION}/$volunteerId"

                val petsSnapshot = firestore.collection(FirebaseKeys.PET_COLLECTION)
                    .whereEqualTo("volunteer", volunteerPath)
                    .get()
                    .await()

                val petIds = petsSnapshot.documents.mapNotNull { it.id }
                val totalPets = petIds.size

                val adoptedPetsSnapshot = firestore.collection(FirebaseKeys.PET_COLLECTION)
                    .whereEqualTo("volunteer", volunteerPath)
                    .whereEqualTo("adopted", true)
                    .get()
                    .await()

                val adoptedPets = adoptedPetsSnapshot.documents.size

                val uniqueShelters = mutableSetOf<String>()

                if (petIds.isNotEmpty()) {
                    for (batch in petIds.chunked(10)) {
                        val viewsSnapshot = firestore.collection(FirebaseKeys.PET_VIEWS_COLLECTION)
                            .whereIn("petId", batch)
                            .get()
                            .await()

                        viewsSnapshot.documents.forEach { document ->
                            document.getString("shelterId")?.let { shelterIdValue ->
                                uniqueShelters.add(shelterIdValue)
                            }
                        }
                    }
                }

                val result = VolunteerDashboardResult(
                    totalPets = totalPets,
                    adoptedPets = adoptedPets,
                    totalViews = uniqueShelters.size
                )

                emit(Resource.Success(result))
            } catch (e: Exception) {
                val errorResource: Resource.Error<VolunteerDashboardResult> = when (e) {
                    is FirebaseFirestoreException -> {
                        if (e.cause is IOException) {
                            Resource.Error(
                                message = e.message.toString(),
                                messageResId = R.string.internet_error
                            )
                        } else {
                            Resource.Error(
                                message = e.message.toString(),
                                messageResId = R.string.dashboard_data_failed
                            )
                        }
                    }
                    is IllegalStateException ->
                        Resource.Error(
                            message = e.message.toString(),
                            messageResId = R.string.dashboard_error_internal
                        )
                    else -> {
                        val genericMessage = e.localizedMessage
                        if (genericMessage != null) {
                            Resource.Error(message = genericMessage)
                        } else {
                            Resource.Error(
                                message = e.message.toString(),
                                messageResId = R.string.error_unknown
                            )
                        }
                    }
                }
                emit(errorResource)
            }
        }.catch { e ->
            val errorResource: Resource.Error<VolunteerDashboardResult> = when (e) {
                is IOException -> Resource.Error(
                    message = e.message.toString(),
                    messageResId = R.string.internet_error
                )
                else -> Resource.Error(
                    message = e.message ?: "Unknown error occurred",
                    messageResId = R.string.error_unknown
                )
            }
            emit(errorResource)
        }
    }

    override fun getVolunteerPets(volunteerId: String): Flow<PagingData<Pet>> {
        val pagingConfig = PagingConfig(
            pageSize = 10,
            prefetchDistance = 5,
            enablePlaceholders = false,
            initialLoadSize = 10
        )

        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                VolunteerPetPagingSource(firestore, volunteerId)
            }
        ).flow
    }

    override fun updatePet(pet: Pet): Flow<Resource<PetResult>> {
        return flow {
            emit(Resource.Loading())

            try {
                if (pet.id == null) {
                    emit(Resource.Error("Pet ID cannot be null"))
                    return@flow
                }

                val petRef = firestore.collection(FirebaseKeys.PET_COLLECTION).document(pet.id)

                val petDoc = petRef.get().await()
                if (!petDoc.exists()) {
                    emit(Resource.Error(
                        message = "Pet not found",
                        messageResId = R.string.pet_not_found
                    ))
                    return@flow
                }

                petRef.set(pet).await()

                emit(Resource.Success(PetResult(
                    message = context.getString(R.string.pet_update_success),
                    pet = pet
                )))
            } catch (e: Exception) {
                val errorResource: Resource.Error<PetResult> = when (e) {
                    is FirebaseFirestoreException -> {
                        if (e.cause is IOException) {
                            Resource.Error(
                                message = e.message.toString(),
                                messageResId = R.string.internet_error
                            )
                        } else {
                            Resource.Error(
                                message = e.message.toString(),
                                messageResId = R.string.pet_update_error_internal
                            )
                        }
                    }
                    else -> {
                        Resource.Error(
                            message = e.message ?: "Unknown error occurred",
                            messageResId = R.string.pet_update_error
                        )
                    }
                }
                emit(errorResource)
            }
        }.catch { e ->
            val errorResource: Resource.Error<PetResult> = when (e) {
                is IOException -> Resource.Error(
                    message = e.message.toString(),
                    messageResId = R.string.internet_error
                )
                else -> Resource.Error(
                    message = e.message ?: "Unknown error occurred",
                    messageResId = R.string.error_unknown
                )
            }
            emit(errorResource)
        }
    }

    override fun togglePetAdopted(
        petId: String,
        isAdopted: Boolean
    ): Flow<Resource<Result>> {
        return flow {
            emit(Resource.Loading<Result>())

            try {
                if (petId.isBlank()) {
                    emit(Resource.Error<Result>(
                        message = "Pet ID cannot be empty",
                        messageResId = R.string.error_empty_pet_id
                    ))
                    return@flow
                }

                val petRef = firestore.collection(FirebaseKeys.PET_COLLECTION).document(petId)
                val petDoc = petRef.get().await()

                if (!petDoc.exists()) {
                    emit(Resource.Error<Result>(
                        message = "Pet not found",
                        messageResId = R.string.pet_not_found
                    ))
                    return@flow
                }

                petRef.update("adopted", isAdopted).await()

                val message = if (isAdopted) {
                    context.getString(R.string.set_as_adopted)
                } else {
                    context.getString(R.string.set_as_available)
                }

                emit(Resource.Success(Result(message)))
            } catch (e: Exception) {
                val errorResource: Resource.Error<Result> = when (e) {
                    is FirebaseFirestoreException -> {
                        if (e.cause is IOException) {
                            Resource.Error<Result>(
                                message = e.message.toString(),
                                messageResId = R.string.internet_error
                            )
                        } else {
                            Resource.Error<Result>(
                                message = e.message.toString(),
                                messageResId = R.string.pet_update_error_internal
                            )
                        }
                    }
                    else -> {
                        Resource.Error<Result>(
                            message = e.message ?: "Unknown error occurred",
                            messageResId = R.string.pet_update_error
                        )
                    }
                }
                emit(errorResource)
            }
        }.catch { e ->
            val errorResource: Resource.Error<Result> = when (e) {
                is IOException -> Resource.Error<Result>(
                    message = e.message.toString(),
                    messageResId = R.string.internet_error
                )
                else -> Resource.Error<Result>(
                    message = e.message ?: "Unknown error occurred",
                    messageResId = R.string.error_unknown
                )
            }
            emit(errorResource)
        }
    }
}