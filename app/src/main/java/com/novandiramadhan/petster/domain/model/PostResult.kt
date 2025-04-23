package com.novandiramadhan.petster.domain.model

data class PostResult(
    val post: Post? = null,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isLiked: Boolean = false,
    val author: UserResult? = null
)