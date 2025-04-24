package com.novandiramadhan.petster.domain.model

data class PostResult(
    val post: Post? = null,
    var likeCount: Int = 0,
    var commentCount: Int = 0,
    var isLiked: Boolean = false
)