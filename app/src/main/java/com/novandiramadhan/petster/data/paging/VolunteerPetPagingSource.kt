package com.novandiramadhan.petster.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.novandiramadhan.petster.common.FirebaseKeys
import com.novandiramadhan.petster.domain.model.Pet
import kotlinx.coroutines.tasks.await
import java.io.IOException

class VolunteerPetPagingSource(
    private val firestore: FirebaseFirestore,
    private val volunteerId: String? = null
): PagingSource<DocumentSnapshot, Pet>() {
    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Pet>): DocumentSnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Pet> {
        return try {
            val pageSize = params.loadSize
            val startAfterDocument = params.key

            if (volunteerId == null) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }

            val volunteerPath = "${FirebaseKeys.VOLUNTEER_COLLECTION}/$volunteerId"

            var query = firestore.collection(FirebaseKeys.PET_COLLECTION)
                .whereEqualTo("volunteer", volunteerPath)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())

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

                    for (doc in viewsSnapshot.documents) {
                        val petId = doc.getString("petId")
                        if (petId != null) {
                            viewCounts[petId] = (viewCounts[petId] ?: 0) + 1
                        }
                    }
                }
            }

            val updatedPets = pets.map { pet ->
                pet.copy(
                    viewCount = pet.id?.let { viewCounts[it] } ?: 0
                )
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
                    "Could not load volunteer pets. Error: ${e.code}. Please try again."
            }
            LoadResult.Error(PetPagingError(errorMessage))
        } catch (_: IOException) {
            LoadResult.Error(PetPagingError("Network error. Please check your connection and try again."))
        } catch (_: Exception) {
            LoadResult.Error(PetPagingError("An unexpected error occurred while loading volunteer pets. Please try again."))
        }
    }
}