package com.novandiramadhan.petster.domain.interactor

import androidx.paging.PagingData
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
}