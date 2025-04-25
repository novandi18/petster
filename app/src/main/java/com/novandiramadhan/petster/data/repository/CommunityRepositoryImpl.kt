package com.novandiramadhan.petster.data.repository

import android.content.Context
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.FirebaseKeys
import com.novandiramadhan.petster.data.paging.CommunityPagingSource
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Post
import com.novandiramadhan.petster.domain.model.PostComment
import com.novandiramadhan.petster.domain.model.PostLike
import com.novandiramadhan.petster.domain.model.PostResult
import com.novandiramadhan.petster.domain.model.Shelter
import com.novandiramadhan.petster.domain.model.UserResult
import com.novandiramadhan.petster.domain.model.Volunteer
import com.novandiramadhan.petster.domain.repository.CommunityRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val firestore: FirebaseFirestore,
    private val generativeModel: GenerativeModel
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

    override fun getPostById(postId: String, currentUserId: String): Flow<Resource<PostResult>> = flow {
        emit(Resource.Loading())

        try {
            val postRef = firestore.collection(FirebaseKeys.POSTS_COLLECTION).document(postId)
            val postSnapshot = postRef.get().await()

            if (postSnapshot.exists()) {
                val post = postSnapshot.toObject(Post::class.java)?.copy(id = postSnapshot.id)

                val commentsSnapshot = postRef.collection(FirebaseKeys.POST_COMMENTS_COLLECTION)
                    .orderBy("createdAt", Query.Direction.ASCENDING)
                    .get()
                    .await()
                val initialComments = commentsSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(PostComment::class.java)?.copy(id = doc.id)
                }

                val commentsWithAuthors = coroutineScope {
                    initialComments.map { comment ->
                        async {
                            var commentAuthor: UserResult? = null
                            if (!comment.authorId.isNullOrEmpty() && !comment.authorType.isNullOrEmpty()) {
                                val collection = when (comment.authorType) {
                                    "volunteer" -> FirebaseKeys.VOLUNTEER_COLLECTION
                                    "shelter" -> FirebaseKeys.SHELTER_COLLECTION
                                    else -> null
                                }
                                if (collection != null) {
                                    try {
                                        val authorDoc = firestore.collection(collection)
                                            .document(comment.authorId)
                                            .get()
                                            .await()
                                        if (authorDoc.exists()) {
                                            commentAuthor = when (comment.authorType) {
                                                "volunteer" -> authorDoc.toObject(Volunteer::class.java)?.copy(uuid = authorDoc.id)?.let { UserResult.VolunteerResult(it) }
                                                "shelter" -> authorDoc.toObject(Shelter::class.java)?.copy(uuid = authorDoc.id)?.let { UserResult.ShelterResult(it) }
                                                else -> null
                                            }
                                        }
                                    } catch (e: Exception) {
                                         Log.e("CommunityRepository", "Error fetching author for comment ${comment.id}", e)
                                    }
                                }
                            }
                            comment.copy(author = commentAuthor)
                        }
                    }.awaitAll()
                }

                val likesSnapshot = postRef.collection(FirebaseKeys.POST_LIKES_COLLECTION).get().await()
                val likes = likesSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(PostLike::class.java)?.copy(id = doc.id)
                }
                val isLiked = likesSnapshot.documents.any { it.id == currentUserId }

                val postAuthor = when(post?.authorType) {
                    "volunteer" -> {
                        val userDoc = firestore.collection(FirebaseKeys.VOLUNTEER_COLLECTION)
                            .document(post.authorId ?: "")
                            .get()
                            .await()
                        userDoc.toObject(Volunteer::class.java)?.copy(uuid = userDoc.id)?.let { UserResult.VolunteerResult(it) }
                    }
                    "shelter" -> {
                        val shelterDoc = firestore.collection(FirebaseKeys.SHELTER_COLLECTION)
                            .document(post.authorId ?: "")
                            .get()
                            .await()
                        shelterDoc.toObject(Shelter::class.java)?.copy(uuid = shelterDoc.id)?.let { UserResult.ShelterResult(it) }
                    }
                    else -> null
                }

                val completePost = post?.copy(
                    author = postAuthor,
                    comments = commentsWithAuthors,
                    likes = likes
                )

                val postResult = PostResult(
                    post = completePost,
                    likeCount = likes.size,
                    commentCount = commentsWithAuthors.size,
                    isLiked = isLiked,
                )

                emit(Resource.Success(postResult))
            } else {
                emit(Resource.Error(context.getString(R.string.community_post_not_found)))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: context.getString(R.string.community_error_unexpected)))
        }
    }

    override fun togglePostLike(
        postId: String,
        uuid: String,
        isLike: Boolean
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val postRef = firestore.collection(FirebaseKeys.POSTS_COLLECTION).document(postId)
            val likeRef = postRef.collection(FirebaseKeys.POST_LIKES_COLLECTION).document(uuid)

            if (isLike) {
                val likeData = mapOf("likedAt" to FieldValue.serverTimestamp())
                likeRef.set(likeData).await()
                Log.d("CommunityRepository", "Post $postId liked by user $uuid")
            } else {
                likeRef.delete().await()
                Log.d("CommunityRepository", "Post $postId unliked by user $uuid")
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e("CommunityRepository", "Error toggling like for post $postId by user $uuid", e)
            emit(Resource.Error(e.message ?: context.getString(R.string.community_error_like_failed)))
        }
    }

    override fun addComment(
        postId: String,
        comment: PostComment
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            val postRef = firestore.collection(FirebaseKeys.POSTS_COLLECTION).document(postId)

            val commentData = mapOf(
                "authorId" to comment.authorId,
                "authorType" to comment.authorType,
                "comment" to comment.comment,
                "replyToCommentId" to comment.replyToCommentId,
                "createdAt" to FieldValue.serverTimestamp()
            )

            val result = postRef.collection(FirebaseKeys.POST_COMMENTS_COLLECTION)
                .add(commentData)
                .await()

            Log.d("CommunityRepository", "Comment added to post $postId with ID: ${result.id}")
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e("CommunityRepository", "Error adding comment to post $postId", e)
            emit(Resource.Error(e.message ?: context.getString(R.string.community_error_comment_failed)))
        }
    }

    override fun generateAIPost(prompt: String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())

            try {
                val content = content {
                    text(prompt)
                }
                val response = generativeModel.generateContent(content)
                val generatedText = response.text?.trim() ?: ""

                if (generatedText.isNotEmpty()) {
                    emit(Resource.Success(generatedText))
                } else {
                    emit(Resource.Error(context.getString(R.string.community_error_ai_empty_response)))
                }
            } catch (e: Exception) {
                Log.e("CommunityRepository", "Error generating AI content", e)
                emit(Resource.Error(e.message ?: context.getString(R.string.community_error_ai_failed)))
            }
        }
    }

    override fun addPost(post: Post): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        try {
            val postData = mapOf(
                "authorId" to post.authorId,
                "authorType" to post.authorType,
                "content" to post.content,
                "createdAt" to FieldValue.serverTimestamp()
            )

            val result = firestore.collection(FirebaseKeys.POSTS_COLLECTION)
                .add(postData)
                .await()

            Log.d("CommunityRepository", "Post added with ID: ${result.id}")
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e("CommunityRepository", "Error adding post", e)
            emit(Resource.Error(e.message ?: context.getString(R.string.community_error_post_failed)))
        }
    }
}