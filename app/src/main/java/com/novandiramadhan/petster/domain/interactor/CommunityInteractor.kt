package com.novandiramadhan.petster.domain.interactor

import androidx.paging.PagingData
import com.novandiramadhan.petster.domain.model.Post
import com.novandiramadhan.petster.domain.repository.CommunityRepository
import com.novandiramadhan.petster.domain.usecase.CommunityUseCase
import kotlinx.coroutines.flow.Flow

class CommunityInteractor(
    private val communityRepository: CommunityRepository
): CommunityUseCase {
    override fun getPosts(): Flow<PagingData<Post>> =
        communityRepository.getPosts()
}