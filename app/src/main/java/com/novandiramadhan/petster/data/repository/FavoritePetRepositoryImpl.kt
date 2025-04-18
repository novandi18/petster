package com.novandiramadhan.petster.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.novandiramadhan.petster.common.FirebaseKeys
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.domain.model.Result
import com.novandiramadhan.petster.domain.repository.FavoritePetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FavoritePetRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): FavoritePetRepository {
    override fun getFavoritePets(shelterId: String): Flow<Resource<List<Pet>>> {
        return flow {
            emit(Resource.Loading())

            val favoritedSnapshot = firestore.collection(FirebaseKeys.FAVORITED_PET_COLLECTION)
                .whereEqualTo("shelterId", shelterId)
                .get()
                .await()

            val petIds = favoritedSnapshot.documents.mapNotNull { doc ->
                doc.getString("petId")
            }

            if (petIds.isEmpty()) {
                emit(Resource.Success(emptyList()))
                return@flow
            }

            val petsList = mutableListOf<Pet>()

            for (id in petIds) {
                val petDoc = firestore.collection(FirebaseKeys.PET_COLLECTION)
                    .document(id)
                    .get()
                    .await()

                val pet = petDoc.toObject(Pet::class.java)?.copy(id = id, isFavorite = true)
                if (pet != null) {
                    petsList.add(pet)
                }
            }

            emit(Resource.Success(petsList.toList()))
        }.catch { e ->
            emit(Resource.Error(e.message ?: "Error fetching favorite pets"))
        }
    }

    override fun toggleFavoritePet(
        petId: String,
        shelterId: String,
        isFavorite: Boolean
    ): Flow<Resource<Result>> {
        return flow {
            emit(Resource.Loading())
            try {
                if (petId.isBlank() || shelterId.isBlank()) {
                    emit(Resource.Error("Pet ID and Shelter ID cannot be empty"))
                    return@flow
                }

                val existingFavorite = firestore.collection(FirebaseKeys.FAVORITED_PET_COLLECTION)
                    .whereEqualTo("petId", petId)
                    .whereEqualTo("shelterId", shelterId)
                    .limit(1)
                    .get()
                    .await()

                if (isFavorite) {
                    if (existingFavorite.isEmpty) {
                        val favoriteData = hashMapOf(
                            "petId" to petId,
                            "shelterId" to shelterId,
                            "timestamp" to com.google.firebase.Timestamp.now()
                        )

                        firestore.collection(FirebaseKeys.FAVORITED_PET_COLLECTION)
                            .document()
                            .set(favoriteData)
                            .await()
                    }
                    emit(Resource.Success(Result("Pet added to favorites successfully")))
                } else {
                    if (!existingFavorite.isEmpty) {
                        val docToDelete = existingFavorite.documents.first()
                        docToDelete.reference.delete().await()
                    }
                    emit(Resource.Success(Result("Pet removed from favorites successfully")))
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is IllegalArgumentException -> "Invalid parameters provided"
                    is com.google.firebase.firestore.FirebaseFirestoreException -> "Database error: ${e.message}"
                    else -> e.message ?: "Failed to update pet favorite status"
                }
                emit(Resource.Error(errorMessage))
            }
        }.catch { e ->
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }
}