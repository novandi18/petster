package com.novandiramadhan.petster.data.repository

import com.novandiramadhan.petster.data.mapper.ImgbbMapper
import com.novandiramadhan.petster.data.remote.RemoteDataSource
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.data.remote.request.ImgBBRequest
import com.novandiramadhan.petster.data.remote.response.ImgBBResponse
import com.novandiramadhan.petster.data.resource.ApiResource
import com.novandiramadhan.petster.data.resource.NetworkOnlyResource
import com.novandiramadhan.petster.domain.model.Imgbb
import com.novandiramadhan.petster.domain.repository.PetImageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetImageRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
): PetImageRepository {
    override fun upload(request: ImgBBRequest): Flow<Resource<Imgbb>> =
        object : NetworkOnlyResource<Imgbb, ImgBBResponse>() {
            override fun loadFromNetwork(data: ImgBBResponse): Flow<Imgbb> =
                ImgbbMapper.mapResponseToModel(data)

            override suspend fun createCall(): Flow<ApiResource<ImgBBResponse>> =
                remoteDataSource.uploadPetImage(request)
        }.asFlow()
}