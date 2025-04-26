package com.novandiramadhan.petster.domain.interactor

import androidx.paging.PagingData
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Post
import com.novandiramadhan.petster.domain.model.PostComment
import com.novandiramadhan.petster.domain.model.PostResult
import com.novandiramadhan.petster.domain.repository.CommunityRepository
import com.novandiramadhan.petster.domain.usecase.CommunityUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CommunityInteractor @Inject constructor(
    private val communityRepository: CommunityRepository
): CommunityUseCase {
    override fun getPosts(uuid: String): Flow<PagingData<PostResult>> =
        communityRepository.getPosts(uuid)

    override fun getPostById(postId: String, currentUserId: String): Flow<Resource<PostResult>> =
        communityRepository.getPostById(postId, currentUserId)

    override fun togglePostLike(
        postId: String,
        uuid: String,
        isLike: Boolean
    ): Flow<Resource<Unit>> = communityRepository.togglePostLike(postId, uuid, isLike)

    override fun addComment(
        postId: String,
        comment: PostComment
    ): Flow<Resource<Unit>> = communityRepository.addComment(postId, comment)

    override fun generateAIPost(prompt: String): Flow<Resource<String>> =
        communityRepository.generateAIPost(prompt)

    override fun addPost(post: Post): Flow<Resource<Unit>> =
        communityRepository.addPost(post)

    override fun deletePost(postId: String): Flow<Resource<Unit>> =
        communityRepository.deletePost(postId)

    override fun updatePost(
        postId: String,
        post: Post
    ): Flow<Resource<Unit>> = communityRepository.updatePost(postId, post)
}