package com.novandiramadhan.petster.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.novandiramadhan.petster.common.RoomConstants

@Entity(tableName = RoomConstants.CHAT_TABLE_NAME)
data class AssistantEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val message: String,
    val role: String,
    val direction: Boolean,
    val userPromptId: String?,
    val timestamp: Long
)