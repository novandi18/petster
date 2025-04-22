package com.novandiramadhan.petster.domain.repository

import androidx.paging.PagingData
import com.novandiramadhan.petster.common.states.PetFilterState
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.domain.model.PetHome
import com.novandiramadhan.petster.domain.model.PetResult
import com.novandiramadhan.petster.domain.model.PetView
import com.novandiramadhan.petster.domain.model.Result
import com.novandiramadhan.petster.domain.model.ShelterLocation
import com.novandiramadhan.petster.domain.model.VolunteerDashboardResult
import kotlinx.coroutines.flow.Flow

interface PetRepository {
    fun addPet(pet: Pet): Flow<Resource<PetResult>>
    fun deletePet(petId: String): Flow<Resource<Result>>
    fun getPetsHome(limitEachCategory: Int, shelterId: String): Flow<Resource<PetHome>>
    fun getPets(
        shelterId: String? = null,
        filter: PetFilterState? = null,
        shelterLocation: ShelterLocation? = null
    ): Flow<PagingData<Pet>>
    fun getPetById(id: String, shelterId: String?): Flow<Resource<Pet>>
    fun addViewedPet(petView: PetView): Flow<Resource<Unit>>
    fun getVolunteerDashboard(volunteerId: String): Flow<Resource<VolunteerDashboardResult>>
    fun getVolunteerPets(volunteerId: String): Flow<PagingData<Pet>>
    fun updatePet(pet: Pet): Flow<Resource<PetResult>>
    fun togglePetAdopted(petId: String, isAdopted: Boolean): Flow<Resource<Result>>
}