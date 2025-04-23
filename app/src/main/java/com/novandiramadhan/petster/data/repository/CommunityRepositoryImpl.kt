package com.novandiramadhan.petster.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.FirebaseFirestore
import com.novandiramadhan.petster.data.paging.CommunityPagingSource
import com.novandiramadhan.petster.domain.model.PostResult
import com.novandiramadhan.petster.domain.repository.CommunityRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val firestore: FirebaseFirestore
): CommunityRepository {
    override fun getPosts(uuid: String): Flow<PagingData<PostResult>> {
        val pagingConfig = PagingConfig(
            pageSize = 10,
            prefetchDistance = 5,
            enablePlaceholders = false,
            initialLoadSize = 10
        )

        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                CommunityPagingSource(
                    context = context,
                    firestore = firestore,
                    uuid = uuid
                )
            }
        ).flow
    }
}