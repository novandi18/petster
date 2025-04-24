package com.novandiramadhan.petster.domain.repository

import androidx.paging.PagingData
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.PostResult
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun getPosts(uuid: String): Flow<PagingData<PostResult>>
    fun getPostById(postId: String, currentUserId: String): Flow<Resource<PostResult>>
}