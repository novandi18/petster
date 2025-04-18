package com.novandiramadhan.petster.data.mapper

import com.novandiramadhan.petster.data.remote.response.ImgBBResponse
import com.novandiramadhan.petster.domain.model.Imgbb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object ImgbbMapper {
    fun mapResponseToModel(response: ImgBBResponse): Flow<Imgbb> = flowOf(
        Imgbb(
            id = response.data.id,
            url = response.data.url
        )
    )
}