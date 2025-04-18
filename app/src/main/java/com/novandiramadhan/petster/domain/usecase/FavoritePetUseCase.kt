package com.novandiramadhan.petster.domain.usecase

import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface FavoritePetUseCase {
    fun getFavoritePets(shelterId: String): Flow<Resource<List<Pet>>>
    fun toggleFavoritePet(petId: String, shelterId: String, isFavorite: Boolean): Flow<Resource<Result>>
}