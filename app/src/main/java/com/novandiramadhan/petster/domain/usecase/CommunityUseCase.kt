package com.novandiramadhan.petster.domain.usecase

import androidx.paging.PagingData
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Post
import com.novandiramadhan.petster.domain.model.PostComment
import com.novandiramadhan.petster.domain.model.PostResult
import kotlinx.coroutines.flow.Flow

interface CommunityUseCase {
    fun getPosts(uuid: String): Flow<PagingData<PostResult>>
    fun getPostById(postId: String, currentUserId: String): Flow<Resource<PostResult>>
    fun togglePostLike(postId: String, uuid: String, isLike: Boolean): Flow<Resource<Unit>>
    fun addComment(postId: String, comment: PostComment): Flow<Resource<Unit>>
    fun generateAIPost(prompt: String): Flow<Resource<String>>
    fun addPost(post: Post): Flow<Resource<Unit>>
    fun deletePost(postId: String): Flow<Resource<Unit>>
    fun updatePost(postId: String, post: Post): Flow<Resource<Unit>>
}