package com.novandiramadhan.petster.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Post
import com.novandiramadhan.petster.domain.usecase.CommunityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityUpdatePostViewModel @Inject constructor(
    private val communityUseCase: CommunityUseCase
): ViewModel() {
    private val _updatePost = MutableStateFlow<Resource<Unit>?>(null)
    val updatePost = _updatePost.asStateFlow()

    private val _postText = MutableStateFlow("")
    val postText = _postText.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _activeGenerationOption = MutableStateFlow<String?>(null)
    val activeGenerationOption = _activeGenerationOption.asStateFlow()

    fun updatePostText(text: String) {
        _postText.value = text
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

    fun updatePost(postId: String, content: String) {
        val post = Post(
            id = postId,
            content = content,
        )

        viewModelScope.launch {
            communityUseCase.updatePost(postId, post)
                .catch { e ->
                    _updatePost.value = Resource.Error(e.message.toString())
                }
                .collect { resource ->
                    _updatePost.value = resource
                }
        }
    }
}