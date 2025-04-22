package com.novandiramadhan.petster.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.novandiramadhan.petster.common.FirebaseKeys
import com.novandiramadhan.petster.common.states.PetFilterState
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.domain.model.ShelterLocation
import com.novandiramadhan.petster.domain.model.Volunteer
import com.novandiramadhan.petster.domain.model.VolunteerLocation
import kotlinx.coroutines.tasks.await
import java.io.IOException
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class PetPagingSource(
    private val firestore: FirebaseFirestore,
    private val shelterId: String? = null,
    private val filter: PetFilterState? = null,
    private val shelterLocation: ShelterLocation? = null,
    private val radiusKm: Double = 10.0
): PagingSource<DocumentSnapshot, Pet>() {

    private fun calculateDistanceKm(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val radLat1 = Math.toRadians(lat1)
        val radLat2 = Math.toRadians(lat2)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(radLat1) * cos(radLat2) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }

    private fun extractIdFromPath(path: String?): String? {
        return path?.substringAfterLast('/')
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Pet>): DocumentSnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Pet> {
        return try {
            val requestedPageSize = params.loadSize
            val queryPageSize = if (shelterLocation != null) {
                requestedPageSize * 3
            } else {
                requestedPageSize
            }

            val startAfterDocument = params.key

            var query = firestore.collection(FirebaseKeys.PET_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .whereEqualTo("adopted", false)

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
                        "< Rp 500rb" -> query = query.whereLessThan("adoptionFee", 500000)
                        "Rp 500rb - 1jt" -> {
                            query = query.whereGreaterThanOrEqualTo("adoptionFee", 500000)
                                .whereLessThanOrEqualTo("adoptionFee", 1000000)
                        }
                        "> Rp 1jt" -> query = query.whereGreaterThan("adoptionFee", 1000000)
                    }
                }
                filter.selectedVaccinated?.let { vaccinated ->
                    when (vaccinated) {
                        "Yes" -> query = query.whereEqualTo("vaccinated", true)
                        "No" -> query = query.whereEqualTo("vaccinated", false)
                    }
                }
            }

            query = query.limit(queryPageSize.toLong())

            if (startAfterDocument != null) {
                query = query.startAfter(startAfterDocument)
            }

            val querySnapshot = query.get().await()
            val lastVisible = if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents.last()
            } else null

            val initialPets = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Pet::class.java)?.copy(id = document.id)
            }
            Log.d("PetPagingSource", "Initial Firestore query returned ${initialPets.size} pets")

            if (initialPets.isEmpty()) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }

            val petIds = initialPets.mapNotNull { it.id }
            val viewCounts = mutableMapOf<String, Int>()
            if (petIds.isNotEmpty()) {
                val petIdBatches = petIds.chunked(10)
                for (batch in petIdBatches) {
                    try {
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
                    } catch (e: Exception) {
                        Log.e("PetPagingSource", "Error fetching view counts for batch: $batch", e)
                    }
                }
            }
            Log.d("PetPagingSource", "Fetched view counts: $viewCounts")

            val favoritedPetIds = mutableSetOf<String>()
            if (shelterId != null) {
                try {
                    val favoritesSnapshot = firestore.collection(FirebaseKeys.FAVORITED_PET_COLLECTION)
                        .whereEqualTo("shelterId", shelterId)
                        .get()
                        .await()
                    for (doc in favoritesSnapshot.documents) {
                        doc.getString("petId")?.let { petId ->
                            favoritedPetIds.add(petId)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PetPagingSource", "Error fetching favorites for shelter: $shelterId", e)
                }
            }
            Log.d("PetPagingSource", "Fetched favorite pet IDs: $favoritedPetIds")

            val updatedPetsWithFavorites = initialPets.map { pet ->
                pet.copy(
                    isFavorite = pet.id != null && pet.id in favoritedPetIds,
                    viewCount = pet.id?.let { viewCounts[it] } ?: 0
                )
            }
            Log.d("PetPagingSource", "Pets after merging views/favorites: ${updatedPetsWithFavorites.size}")

            val finalPets: List<Pet>
            if (shelterLocation != null) {
                Log.d("PetPagingSource", "Applying geofencing filter. Radius: $radiusKm km")

                val volunteerUuids = updatedPetsWithFavorites.mapNotNull { extractIdFromPath(it.volunteer) }.distinct()
                Log.d("PetPagingSource", "Unique Volunteer UUIDs to fetch location for: $volunteerUuids")

                val volunteerLocations = mutableMapOf<String, VolunteerLocation?>()

                if (volunteerUuids.isNotEmpty()) {
                    val volunteerBatches = volunteerUuids.chunked(10)
                    for (batch in volunteerBatches) {
                        try {
                            val volunteersSnapshot = firestore.collection(FirebaseKeys.VOLUNTEER_COLLECTION)
                                .whereIn("uuid", batch)
                                .get()
                                .await()
                            for (doc in volunteersSnapshot.documents) {
                                val volunteer = doc.toObject(Volunteer::class.java)
                                if (volunteer?.uuid != null) {
                                    volunteerLocations[volunteer.uuid] = volunteer.location
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("PetPagingSource", "Error fetching volunteer locations for batch: $batch", e)
                        }
                    }
                }
                Log.d("PetPagingSource", "Fetched volunteer locations map: $volunteerLocations")

                val petsWithDistance = updatedPetsWithFavorites.mapNotNull { pet ->
                    val volunteerUuid = extractIdFromPath(pet.volunteer)
                    if (volunteerUuid != null) {
                        val location = volunteerLocations[volunteerUuid]
                        if (location?.latitude != null && location.longitude != null) {
                            val distance = calculateDistanceKm(
                                shelterLocation.latitude, shelterLocation.longitude,
                                location.latitude, location.longitude
                            )
                            if (distance <= radiusKm) {
                                Pair(pet, distance)
                            } else {
                                null
                            }
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }
                Log.d("PetPagingSource", "Found ${petsWithDistance.size} pets within ${radiusKm}km before sorting/taking")

                finalPets = petsWithDistance
                    .sortedBy { it.second }
                    .take(requestedPageSize)
                    .map { it.first }
                Log.d("PetPagingSource", "Final pets after distance sort and take: ${finalPets.size}")

            } else {
                finalPets = updatedPetsWithFavorites
                Log.d("PetPagingSource", "Geofencing not active. Final pets count: ${finalPets.size}")
            }

            LoadResult.Page(
                data = finalPets,
                prevKey = null,
                nextKey = if (finalPets.isEmpty() && querySnapshot.documents.size < queryPageSize) null else lastVisible
            )

        } catch (e: FirebaseFirestoreException) {
            Log.e("PetPagingSource", "Firestore error loading pets", e)
            val errorMessage = when (e.code) {
                FirebaseFirestoreException.Code.UNAVAILABLE -> "Network error. Please check your connection and try again."
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Access denied. You may not have permission to view this data."
                FirebaseFirestoreException.Code.UNAUTHENTICATED -> "Authentication required. Please log in."
                else -> "Could not load pets due to Firestore error: ${e.code}"
            }
            LoadResult.Error(PetPagingError(errorMessage))
        } catch (e: IOException) {
            Log.e("PetPagingSource", "Network error loading pets", e)
            LoadResult.Error(PetPagingError("Network error. Please check your connection and try again."))
        } catch (e: Exception) {
            Log.e("PetPagingSource", "Unexpected error loading pets", e)
            LoadResult.Error(PetPagingError("An unexpected error occurred while loading pets: ${e.message}"))
        }
    }
}