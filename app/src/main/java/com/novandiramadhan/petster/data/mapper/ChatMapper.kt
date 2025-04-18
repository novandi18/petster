package com.novandiramadhan.petster.data.mapper

import com.novandiramadhan.petster.data.local.room.entity.AssistantEntity
import com.novandiramadhan.petster.domain.model.Chat

fun AssistantEntity.toDomain(): Chat {
    return Chat(
        id = this.id,
        userId = this.userId,
        message = this.message,
        role = this.role,
        direction = this.direction,
        userPromptId = this.userPromptId,
        timestamp = this.timestamp
    )
}

fun Chat.toEntity(): AssistantEntity {
    return AssistantEntity(
        id = this.id,
        userId = this.userId,
        message = this.message,
        role = this.role,
        direction = this.direction,
        userPromptId = this.userPromptId,
        timestamp = this.timestamp
    )
}

fun List<AssistantEntity>.toDomainList(): List<Chat> {
    return this.map { it.toDomain() }
}