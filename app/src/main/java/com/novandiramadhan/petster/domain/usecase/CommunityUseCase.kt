package com.novandiramadhan.petster.domain.usecase

import androidx.paging.PagingData
import com.novandiramadhan.petster.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface CommunityUseCase {
    fun getPosts(): Flow<PagingData<Post>>
}