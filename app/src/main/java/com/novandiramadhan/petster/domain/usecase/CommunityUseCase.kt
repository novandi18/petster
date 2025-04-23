package com.novandiramadhan.petster.domain.usecase

import androidx.paging.PagingData
import com.novandiramadhan.petster.domain.model.PostResult
import kotlinx.coroutines.flow.Flow

interface CommunityUseCase {
    fun getPosts(uuid: String): Flow<PagingData<PostResult>>
}