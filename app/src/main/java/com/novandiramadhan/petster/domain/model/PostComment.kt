package com.novandiramadhan.petster.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PostComment(
    val id: String? = null,
    val authorId: String? = null,
    val authorType: String? = null,
    val comment: String? = null,
    val replyToCommentId: String? = null,
    val author: UserResult? = null,
    @ServerTimestamp val createdAt: Date? = null
)
