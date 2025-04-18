package com.novandiramadhan.petster.domain.repository

import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.data.remote.request.ImgBBRequest
import com.novandiramadhan.petster.domain.model.Imgbb
import kotlinx.coroutines.flow.Flow

interface PetImageRepository {
    fun upload(request: ImgBBRequest): Flow<Resource<Imgbb>>
}