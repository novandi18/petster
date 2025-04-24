package com.novandiramadhan.petster.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Post(
    val id: String? = null,
    val authorId: String? = null,
    val authorType: String? = null,
    val content: String? = null,
    val comments: List<PostComment>? = null,
    val likes: List<PostLike>? = null,
    val author: UserResult? = null,
    @ServerTimestamp val createdAt: Date? = null
)