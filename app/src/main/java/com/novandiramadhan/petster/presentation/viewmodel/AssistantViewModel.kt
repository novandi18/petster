package com.novandiramadhan.petster.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.types.ChatRoleType
import com.novandiramadhan.petster.domain.datastore.AuthDataStore
import com.novandiramadhan.petster.domain.model.AuthState
import com.novandiramadhan.petster.domain.model.Chat
import com.novandiramadhan.petster.domain.usecase.AssistantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssistantViewModel @Inject constructor(
    application: Application,
    private val assistantUseCase: AssistantUseCase,
    authDataStore: AuthDataStore
): ViewModel() {
    private val authState: StateFlow<AuthState> = authDataStore.state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = AuthState()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val messages: StateFlow<List<Chat>> = authState
        .map { it.uuid }
        .distinctUntilChanged()
        .flatMapLatest { userId ->
            if (userId != null) {
                assistantUseCase.getChatHistory(userId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRetryingMessageId = MutableStateFlow<String?>(null)
    val isRetryingMessageId: StateFlow<String?> = _isRetryingMessageId.asStateFlow()

    private val defaultPromptInstruction = application.getString(R.string.default_prompt_assistant)

    fun onInputTextChanged(newText: String) {
        _inputText.value = newText
    }

    fun sendMessage() {
        val userMessageText = _inputText.value.trim()
        val userId = authState.value.uuid
        if (userMessageText.isBlank() || userId == null) return

        val userMessage = Chat(
            userId = userId,
            message = userMessageText,
            role = ChatRoleType.USER.value,
            direction = true
        )

        val historyForApi = messages.value

        viewModelScope.launch {
            assistantUseCase.saveChatMessage(userMessage)

            _isLoading.value = true
            _inputText.value = ""

            val fullPromptToSend = defaultPromptInstruction + userMessageText
            try {
                val result = assistantUseCase.getApiResponse(fullPromptToSend, historyForApi)

                result.fold(
                    onSuccess = { response ->
                        val assistantResponseText = response.text ?: "Sorry, I couldn't process that."
                        val assistantMessage = Chat(
                            userId = userId,
                            message = assistantResponseText,
                            role = ChatRoleType.MODEL.value,
                            direction = false,
                            userPromptId = userMessage.id
                        )
                        assistantUseCase.saveChatMessage(assistantMessage)
                    },
                    onFailure = { exception ->
                        println("GenAI Error: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                println("GenAI Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun regenerateResponse(messageToRetry: Chat) {
        val userId = authState.value.uuid
        if (userId == null || messageToRetry.userId != userId) return

        viewModelScope.launch {
            val originalUserMessage = messages.value.find { it.id == messageToRetry.userPromptId && it.direction }
            if (originalUserMessage == null) {
                val errorUpdate = messageToRetry.copy(message = "Error: Cannot find original prompt.")
                assistantUseCase.updateChatMessage(errorUpdate)
                return@launch
            }

            val messageIndexToRetry = messages.value.indexOf(messageToRetry)
            if (messageIndexToRetry < 0) return@launch

            _isRetryingMessageId.value = messageToRetry.id
            _isLoading.value = true

            val historyForRetry = messages.value.subList(0, messages.value.indexOf(originalUserMessage))
            val fullPromptForRetry = defaultPromptInstruction + originalUserMessage.message
            try {
                val result = assistantUseCase.getApiResponse(fullPromptForRetry, historyForRetry)

                result.fold(
                    onSuccess = { response ->
                        val newAssistantResponseText = response.text ?: "Sorry, I couldn't process that."
                        val regeneratedMessage = messageToRetry.copy(
                            message = newAssistantResponseText,
                            timestamp = System.currentTimeMillis()
                        )
                        assistantUseCase.updateChatMessage(regeneratedMessage)
                    },
                    onFailure = { exception ->
                        val retryErrorMessage = messageToRetry.copy(
                            message = "Retry Error: ${exception.message ?: "Unknown error"}"
                        )
                        assistantUseCase.updateChatMessage(retryErrorMessage)
                    }
                )
            } catch (e: Exception) {
                println("API Retry Error outside fold: ${e.message}")
                val retryErrorMessage = messageToRetry.copy(
                    message = "Retry Error: ${e.message ?: "Unknown error"}"
                )
                assistantUseCase.updateChatMessage(retryErrorMessage)
            } finally {
                _isRetryingMessageId.value = null
                _isLoading.value = false
            }
        }
    }

    fun clearChat() {
        val userId = authState.value.uuid
        if (userId != null) {
            viewModelScope.launch {
                assistantUseCase.clearHistory(userId)
            }
        }
    }
}