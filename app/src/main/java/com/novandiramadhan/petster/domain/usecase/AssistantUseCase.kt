package com.novandiramadhan.petster.domain.usecase

import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.novandiramadhan.petster.domain.model.Chat
import kotlinx.coroutines.flow.Flow

interface AssistantUseCase {
    fun getChatHistory(shelterId: String): Flow<List<Chat>>
    suspend fun saveChatMessage(message: Chat)
    suspend fun updateChatMessage(message: Chat)
    suspend fun getApiResponse(prompt: String, history: List<Chat>): Result<GenerateContentResponse>
    suspend fun clearHistory(shelterId: String)
}