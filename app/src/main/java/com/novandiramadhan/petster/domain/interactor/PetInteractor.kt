package com.novandiramadhan.petster.domain.interactor

import androidx.paging.PagingData
import com.novandiramadhan.petster.common.states.PetFilterState
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.domain.model.PetHome
import com.novandiramadhan.petster.domain.model.PetResult
import com.novandiramadhan.petster.domain.model.PetView
import com.novandiramadhan.petster.domain.model.Result
import com.novandiramadhan.petster.domain.model.VolunteerDashboardResult
import com.novandiramadhan.petster.domain.repository.PetRepository
import com.novandiramadhan.petster.domain.usecase.PetUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PetInteractor @Inject constructor(
    private val petRepository: PetRepository
): PetUseCase {
    override fun addPet(pet: Pet): Flow<Resource<PetResult>> = petRepository.addPet(pet)

    override fun deletePet(petId: String): Flow<Resource<Result>> = petRepository.deletePet(petId)

    override fun getPetsHome(limitEachCategory: Int, shelterId: String): Flow<Resource<PetHome>> =
        petRepository.getPetsHome(limitEachCategory, shelterId)

    override fun getPets(shelterId: String?, filter: PetFilterState?): Flow<PagingData<Pet>> =
        petRepository.getPets(shelterId, filter)

    override fun getPetById(id: String, shelterId: String?): Flow<Resource<Pet>> =
        petRepository.getPetById(id, shelterId)

    override fun addViewedPet(petView: PetView): Flow<Resource<Unit>> = petRepository.addViewedPet(petView)

    override fun getVolunteerDashboard(volunteerId: String): Flow<Resource<VolunteerDashboardResult>> =
        petRepository.getVolunteerDashboard(volunteerId)

    override fun getVolunteerPets(volunteerId: String): Flow<PagingData<Pet>> =
        petRepository.getVolunteerPets(volunteerId)

    override fun updatePet(pet: Pet): Flow<Resource<PetResult>> = petRepository.updatePet(pet)

    override fun togglePetAdopted(
        petId: String,
        isAdopted: Boolean
    ): Flow<Resource<Result>> = petRepository.togglePetAdopted(petId, isAdopted)
}