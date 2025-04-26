package com.novandiramadhan.petster.domain.repository

import androidx.paging.PagingData
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Post
import com.novandiramadhan.petster.domain.model.PostComment
import com.novandiramadhan.petster.domain.model.PostResult
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun getPosts(uuid: String): Flow<PagingData<PostResult>>
    fun getPostById(postId: String, currentUserId: String): Flow<Resource<PostResult>>
    fun togglePostLike(postId: String, uuid: String, isLike: Boolean): Flow<Resource<Unit>>
    fun addComment(postId: String, comment: PostComment): Flow<Resource<Unit>>
    fun generateAIPost(prompt: String): Flow<Resource<String>>
    fun addPost(post: Post): Flow<Resource<Unit>>
    fun deletePost(postId: String): Flow<Resource<Unit>>
}