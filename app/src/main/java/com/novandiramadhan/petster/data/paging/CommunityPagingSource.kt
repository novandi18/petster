package com.novandiramadhan.petster.data.paging

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.FirebaseKeys
import com.novandiramadhan.petster.domain.model.Post
import kotlinx.coroutines.tasks.await
import java.io.IOException

class CommunityPagingSource(
    private val context: Context,
    private val firestore: FirebaseFirestore,
): PagingSource<DocumentSnapshot, Post>() {
    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Post>): DocumentSnapshot? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Post> {
        return try {
            val pageSize = params.loadSize
            val startAfterDocument = params.key

            var query = firestore.collection(FirebaseKeys.POSTS_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())

            if (startAfterDocument != null) {
                query = query.startAfter(startAfterDocument)
            }

            val querySnapshot = query.get().await()
            val lastVisible = if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents.last()
            } else null

            val posts = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Post::class.java)?.copy(id = document.id)
            }

            Log.d("CommunityPagingSource", "Loaded ${posts.size} posts")

            LoadResult.Page(
                data = posts,
                prevKey = null,
                nextKey = if (posts.isEmpty() || querySnapshot.documents.size < pageSize) null else lastVisible
            )
        } catch (e: FirebaseFirestoreException) {
            Log.e("CommunityPagingSource", "Firestore error loading posts", e)
            val errorMessage = when (e.code) {
                FirebaseFirestoreException.Code.UNAVAILABLE -> context.getString(R.string.community_error_network)
                else -> context.getString(R.string.community_error_unexpected)
            }
            LoadResult.Error(Exception(errorMessage))
        } catch (e: IOException) {
            Log.e("CommunityPagingSource", "Network error loading posts", e)
            LoadResult.Error(Exception(context.getString(R.string.community_error_network)))
        } catch (e: Exception) {
            Log.e("CommunityPagingSource", "Unexpected error loading posts", e)
            LoadResult.Error(Exception(context.getString(R.string.community_error_unexpected)))
        }
    }
}