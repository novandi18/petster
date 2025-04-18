package com.novandiramadhan.petster.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.novandiramadhan.petster.common.FirebaseKeys
import com.novandiramadhan.petster.common.states.PetFilterState
import com.novandiramadhan.petster.domain.model.Pet
import kotlinx.coroutines.tasks.await
import java.io.IOException

class PetPagingSource(
    private val firestore: FirebaseFirestore,
    private val shelterId: String? = null,
    private val filter: PetFilterState? = null
): PagingSource<DocumentSnapshot, Pet>() {
    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Pet>): DocumentSnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Pet> {
        return try {
            val pageSize = params.loadSize
            val startAfterDocument = params.key

            var query = firestore.collection(FirebaseKeys.PET_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)

            if (filter != null) {
                filter.selectedCategory?.let { category ->
                    query = query.whereEqualTo("category", category)
                }

                filter.selectedGender?.let { gender ->
                    query = query.whereEqualTo("gender", gender)
                }

                filter.selectedAdoptionFeeRange?.let { feeRange ->
                    when (feeRange) {
                        "Free" -> query = query.whereEqualTo("adoptionFee", null)
                        "< Rp 500rb" ->
                            query = query.whereLessThan("adoptionFee", 500000)
                        "Rp 500rb - 1jt" -> {
                            query = query.whereGreaterThanOrEqualTo("adoptionFee", 500000)
                                .whereLessThanOrEqualTo("adoptionFee", 1000000)
                        }
                        "> Rp 1jt" ->
                            query = query.whereGreaterThan("adoptionFee", 1000000)
                    }
                }

                filter.selectedVacinated?.let { vacinated ->
                    when (vacinated) {
                        "Yes" -> query = query.whereEqualTo("vaccinated", true)
                        "No" -> query = query.whereEqualTo("vaccinated", false)
                    }
                }
            }

            query = query.limit(pageSize.toLong())

            if (startAfterDocument != null) {
                query = query.startAfter(startAfterDocument)
            }

            val querySnapshot = query.get().await()
            val lastVisible = if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents.last()
            } else null

            val pets = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Pet::class.java)?.copy(id = document.id)
            }

            val petIds = pets.mapNotNull { it.id }
            val viewCounts = mutableMapOf<String, Int>()

            if (petIds.isNotEmpty()) {
                val petIdBatches = petIds.chunked(10)

                for (batch in petIdBatches) {
                    val viewsSnapshot = firestore.collection(FirebaseKeys.PET_VIEWS_COLLECTION)
                        .whereIn("petId", batch)
                        .get()
                        .await()

                    // Count occurrences of each petId
                    for (doc in viewsSnapshot.documents) {
                        val petId = doc.getString("petId")
                        if (petId != null) {
                            viewCounts[petId] = (viewCounts[petId] ?: 0) + 1
                        }
                    }
                }
            }

            val updatedPets = if (shelterId != null) {
                val favoritedPets = mutableSetOf<String>()
                val favoritesSnapshot = firestore.collection(FirebaseKeys.FAVORITED_PET_COLLECTION)
                    .whereEqualTo("shelterId", shelterId)
                    .get()
                    .await()

                for (doc in favoritesSnapshot.documents) {
                    doc.getString("petId")?.let { petId ->
                        favoritedPets.add(petId)
                    }
                }

                pets.map { pet ->
                    pet.copy(
                        isFavorite = pet.id in favoritedPets,
                        viewCount = pet.id?.let { viewCounts[it] } ?: 0
                    )
                }
            } else {
                pets.map { pet ->
                    pet.copy(
                        isFavorite = false,
                        viewCount = pet.id?.let { viewCounts[it] } ?: 0
                    )
                }
            }

            LoadResult.Page(
                data = updatedPets,
                prevKey = null,
                nextKey = lastVisible
            )
        } catch (e: FirebaseFirestoreException) {
            val errorMessage = when (e.code) {
                FirebaseFirestoreException.Code.UNAVAILABLE ->
                    "Network error. Please check your connection and try again."
                FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                    "Access denied. You may not have permission to view this data."
                FirebaseFirestoreException.Code.UNAUTHENTICATED ->
                    "Authentication required. Please log in."
                else ->
                    "Could not load pets."
            }
            LoadResult.Error(PetPagingError(errorMessage))
        } catch (_: IOException) {
            LoadResult.Error(PetPagingError("Network error. Please check your connection and try again."))
        } catch (_: Exception) {
            LoadResult.Error(PetPagingError("An unexpected error occurred while loading pets. Please try again."))
        }
    }
}