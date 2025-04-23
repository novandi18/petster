package com.novandiramadhan.petster.domain.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Comment(
    @DocumentId val id: String? = null,
    val authorId: String? = null,
    val authorType: String? = null,
    val comment: String? = null,
    val replyToCommentId: String? = null,
    @ServerTimestamp val createdAt: Date? = null
)
