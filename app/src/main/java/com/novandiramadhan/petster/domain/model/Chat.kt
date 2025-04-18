package com.novandiramadhan.petster.domain.model

import java.util.UUID

data class Chat(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val message: String,
    val role: String,
    val direction: Boolean,
    val userPromptId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)