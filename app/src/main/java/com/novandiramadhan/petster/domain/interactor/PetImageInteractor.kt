package com.novandiramadhan.petster.domain.interactor

import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.data.remote.request.ImgBBRequest
import com.novandiramadhan.petster.domain.model.Imgbb
import com.novandiramadhan.petster.domain.repository.PetImageRepository
import com.novandiramadhan.petster.domain.usecase.PetImageUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PetImageInteractor @Inject constructor(
    private val petImageRepository: PetImageRepository
): PetImageUseCase {
    override fun uploadImage(imgBBRequest: ImgBBRequest): Flow<Resource<Imgbb>> =
        petImageRepository.upload(imgBBRequest)
}