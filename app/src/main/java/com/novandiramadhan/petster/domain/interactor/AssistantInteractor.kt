package com.novandiramadhan.petster.domain.interactor

import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.novandiramadhan.petster.domain.model.Chat
import com.novandiramadhan.petster.domain.repository.AssistantRepository
import com.novandiramadhan.petster.domain.usecase.AssistantUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AssistantInteractor @Inject constructor(
    private val assistantRepository: AssistantRepository
): AssistantUseCase {
    override fun getChatHistory(shelterId: String): Flow<List<Chat>> =
        assistantRepository.getChatHistory(shelterId)

    override suspend fun saveChatMessage(message: Chat) =
        assistantRepository.saveChatMessage(message)

    override suspend fun updateChatMessage(message: Chat) =
        assistantRepository.updateChatMessage(message)

    override suspend fun getApiResponse(
        prompt: String,
        history: List<Chat>
    ): Result<GenerateContentResponse> =
        assistantRepository.getApiResponse(prompt, history)

    override suspend fun clearHistory(shelterId: String) = assistantRepository.clearHistory(shelterId)
}