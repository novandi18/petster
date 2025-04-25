package com.novandiramadhan.petster.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import com.novandiramadhan.petster.data.local.room.dao.AssistantDao
import com.novandiramadhan.petster.data.mapper.toDomainList
import com.novandiramadhan.petster.data.mapper.toEntity
import com.novandiramadhan.petster.domain.model.Chat
import com.novandiramadhan.petster.domain.repository.AssistantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AssistantRepositoryImpl @Inject constructor(
    private val assistantDao: AssistantDao,
    private val generativeModel: GenerativeModel
): AssistantRepository {

    override fun getChatHistory(shelterId: String): Flow<List<Chat>> =
        assistantDao.getAllMessages(shelterId).map { it.toDomainList() }

    override suspend fun saveChatMessage(message: Chat) {
        withContext(Dispatchers.IO) {
            assistantDao.insertMessage(message.toEntity())
        }
    }

    override suspend fun updateChatMessage(message: Chat) {
        withContext(Dispatchers.IO) {
            assistantDao.updateMessage(message.toEntity())
        }
    }

    override suspend fun getApiResponse(
        prompt: String,
        history: List<Chat>
    ): Result<GenerateContentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val sdkHistory = mapChatToContent(history)
                val chatSession = generativeModel.startChat(history = sdkHistory)
                val response = chatSession.sendMessage(prompt)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun clearHistory(shelterId: String) {
        withContext(Dispatchers.IO) {
            assistantDao.clearHistory(shelterId)
        }
    }

    private fun mapChatToContent(messages: List<Chat>): List<Content> {
        return messages.map { chat ->
            content(role = chat.role) { text(chat.message) }
        }
    }
}