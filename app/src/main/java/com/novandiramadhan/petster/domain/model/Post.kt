package com.novandiramadhan.petster.domain.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Post(
    @DocumentId val id: String? = null,
    val authorId: String? = null,
    val authorType: String? = null,
    val content: String? = null,
    val comments: List<Comment>? = null,
    val likes: List<PostLike>? = null,
    @ServerTimestamp val createdAt: Date? = null
)

data class PostLike(
    @DocumentId val id: String? = null,
    val authorId: String? = null,
    val createdAt: Date? = null,
)