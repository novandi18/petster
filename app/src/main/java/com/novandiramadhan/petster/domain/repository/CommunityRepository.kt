package com.novandiramadhan.petster.domain.repository

import androidx.paging.PagingData
import com.novandiramadhan.petster.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun getPosts(): Flow<PagingData<Post>>
}