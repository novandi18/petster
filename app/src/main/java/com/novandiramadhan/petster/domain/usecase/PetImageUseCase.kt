package com.novandiramadhan.petster.domain.usecase

import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.data.remote.request.ImgBBRequest
import com.novandiramadhan.petster.domain.model.Imgbb
import kotlinx.coroutines.flow.Flow

interface PetImageUseCase {
    fun uploadImage(imgBBRequest: ImgBBRequest): Flow<Resource<Imgbb>>
}