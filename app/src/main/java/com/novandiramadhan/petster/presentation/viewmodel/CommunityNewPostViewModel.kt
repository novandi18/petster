package com.novandiramadhan.petster.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.datastore.AuthDataStore
import com.novandiramadhan.petster.domain.model.AuthState
import com.novandiramadhan.petster.domain.model.Post
import com.novandiramadhan.petster.domain.usecase.CommunityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityNewPostViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val communityUseCase: CommunityUseCase
): ViewModel() {
    private val _authState: MutableStateFlow<AuthState?> = MutableStateFlow(null)
    val authState: StateFlow<AuthState?> = _authState.asStateFlow()

    private val _postStatus = MutableStateFlow<Resource<Unit>?>(null)
    val postStatus = _postStatus.asStateFlow()

    private val _postText = MutableStateFlow("")
    val postText = _postText.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _activeGenerationOption = MutableStateFlow<String?>(null)
    val activeGenerationOption = _activeGenerationOption.asStateFlow()

    fun updatePostText(text: String) {
        _postText.value = text
    }

    init {
        getUserLoggedIn()
    }

    fun getUserLoggedIn() {
        viewModelScope.launch {
            authDataStore.state
                .onStart {
                    Log.d("CommunityNewPostViewModel", "Observing Auth State...")
                }
                .catch { exception ->
                    Log.e("CommunityNewPostViewModel", "Error observing Auth State", exception)
                    _authState.value = AuthState(userType = UserType.NONE)
                }
                .collect { result ->
                    _authState.value = result
                }
        }
    }

    fun generateAIContent(prompt: String, option: String) {
        if (_isGenerating.value) return

        viewModelScope.launch {
            _isGenerating.value = true
            _activeGenerationOption.value = option

            communityUseCase.generateAIPost(prompt)
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            resource.data?.let { generatedText ->
                                _postText.value = generatedText
                            }
                            _isGenerating.value = false
                            _activeGenerationOption.value = null
                        }
                        is Resource.Error -> {
                            _isGenerating.value = false
                            _activeGenerationOption.value = null
                        }
                        is Resource.Loading -> {}
                    }
            }
        }
    }

    fun addPost() {
        val data = Post(
            authorId = authState.value?.uuid,
            authorType = authState.value?.userType?.name?.lowercase(),
            content = postText.value
        )

        viewModelScope.launch {
            communityUseCase.addPost(data)
                .catch { e ->
                    _postStatus.value = Resource.Error(e.message.toString())
                }
                .collect { resource ->
                    _postStatus.value = resource
                }
        }
    }
}