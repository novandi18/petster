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
import com.novandiramadhan.petster.domain.model.PostResult
import com.novandiramadhan.petster.domain.model.Shelter
import com.novandiramadhan.petster.domain.model.UserResult
import com.novandiramadhan.petster.domain.model.Volunteer
import kotlinx.coroutines.tasks.await
import java.io.IOException

class CommunityPagingSource(
    private val context: Context,
    private val firestore: FirebaseFirestore,
    private val uuid: String,
): PagingSource<DocumentSnapshot, PostResult>() {
    override fun getRefreshKey(state: PagingState<DocumentSnapshot, PostResult>): DocumentSnapshot? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, PostResult> {
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

            val postResults = posts.mapNotNull { post ->
                try {
                    val postId = post.id ?: return@mapNotNull null

                    val likesSnapshot = firestore.collection(FirebaseKeys.POSTS_COLLECTION)
                        .document(postId)
                        .collection(FirebaseKeys.POST_LIKES_COLLECTION)
                        .get()
                        .await()

                    val likeCount = likesSnapshot.size()
                    val isLiked = likesSnapshot.documents.any { it.id == uuid }

                    val commentsSnapshot = firestore.collection(FirebaseKeys.POSTS_COLLECTION)
                        .document(postId)
                        .collection(FirebaseKeys.POST_COMMENTS_COLLECTION)
                        .get()
                        .await()

                    val commentCount = commentsSnapshot.size()

                    var author: UserResult? = null
                    if (post.authorId != null && post.authorType != null) {
                        val collection = when (post.authorType) {
                            "volunteer" -> FirebaseKeys.VOLUNTEER_COLLECTION
                            "shelter" -> FirebaseKeys.SHELTER_COLLECTION
                            else -> null
                        }

                        if (collection != null) {
                            val authorDoc = firestore.collection(collection)
                                .document(post.authorId)
                                .get()
                                .await()

                            author = when (post.authorType) {
                                "volunteer" -> {
                                    val volunteer = authorDoc.toObject(Volunteer::class.java)
                                    volunteer?.let { UserResult.VolunteerResult(it) }
                                }
                                "shelter" -> {
                                    val shelter = authorDoc.toObject(Shelter::class.java)
                                    shelter?.let { UserResult.ShelterResult(it) }
                                }
                                else -> null
                            }
                        }
                    }

                    val postWithAuthor = post.copy(
                        author = author
                    )

                    PostResult(
                        post = postWithAuthor,
                        likeCount = likeCount,
                        commentCount = commentCount,
                        isLiked = isLiked
                    )
                } catch (e: Exception) {
                    Log.e("CommunityPagingSource", "Error processing post ${post.id}", e)
                    null
                }
            }

            LoadResult.Page(
                data = postResults,
                prevKey = null,
                nextKey = if (postResults.isEmpty() || querySnapshot.documents.size < pageSize) null else lastVisible
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