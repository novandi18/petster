package com.novandiramadhan.petster.domain.interactor

import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.domain.model.Result
import com.novandiramadhan.petster.domain.repository.FavoritePetRepository
import com.novandiramadhan.petster.domain.usecase.FavoritePetUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoritePetInteractor @Inject constructor(
    private val favoritePetRepository: FavoritePetRepository
): FavoritePetUseCase {
    override fun getFavoritePets(shelterId: String): Flow<Resource<List<Pet>>> =
        favoritePetRepository.getFavoritePets(shelterId)

    override fun toggleFavoritePet(
        petId: String,
        shelterId: String,
        isFavorite: Boolean
    ): Flow<Resource<Result>> = favoritePetRepository.toggleFavoritePet(petId, shelterId, isFavorite)
}