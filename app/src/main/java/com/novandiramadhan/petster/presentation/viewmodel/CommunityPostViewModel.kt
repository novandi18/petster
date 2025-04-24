package com.novandiramadhan.petster.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.datastore.AuthDataStore
import com.novandiramadhan.petster.domain.model.AuthState
import com.novandiramadhan.petster.domain.model.PostResult
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
class CommunityPostViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val communityUseCase: CommunityUseCase
): ViewModel() {
    private val _authState: MutableStateFlow<AuthState?> = MutableStateFlow(null)
    val authState: StateFlow<AuthState?> = _authState.asStateFlow()

    private val _post: MutableStateFlow<Resource<PostResult>?> = MutableStateFlow(null)
    val post: StateFlow<Resource<PostResult>?> = _post.asStateFlow()

    private val _replyToCommentId = MutableStateFlow<String?>(null)
    val replyToCommentId: StateFlow<String?> = _replyToCommentId.asStateFlow()

    private val _replyToAuthorName = MutableStateFlow<String?>(null)
    val replyToAuthorName: StateFlow<String?> = _replyToAuthorName.asStateFlow()

    fun setReplyTo(commentId: String?, authorName: String?) {
        _replyToCommentId.value = commentId
        _replyToAuthorName.value = authorName
    }

    init {
        getUserLoggedIn()
    }

    fun getUserLoggedIn() {
        viewModelScope.launch {
            authDataStore.state
                .onStart {
                    Log.d("CommunityViewModel", "Observing Auth State...")
                }
                .catch { exception ->
                    Log.e("CommunityViewModel", "Error observing Auth State", exception)
                    _authState.value = AuthState(userType = UserType.NONE)
                }
                .collect { result ->
                    _authState.value = result
                }
        }
    }

    fun getPost(postId: String, currentUuid: String) {
        viewModelScope.launch {
            communityUseCase.getPostById(postId, currentUuid)
                .catch { e ->
                    _post.value = Resource.Error(e.message.toString())
                }
                .collect { result ->
                    _post.value = result
                }
        }
    }

    fun toggleLike(postId: String, isLike: Boolean) {
        viewModelScope.launch {
            val currentUuid = _authState.value?.uuid
            if (currentUuid == null) {
                Log.e("CommunityPostViewModel", "Cannot toggle like, user UUID is null")
                return@launch
            }

            val currentState = _post.value
            if (currentState is Resource.Success && currentState.data != null) {
                val currentData = currentState.data
                val updatedData = currentData.copy(
                    isLiked = isLike,
                    likeCount = currentData.likeCount + if (isLike) 1 else -1
                )
                _post.value = Resource.Success(updatedData)
            }

            communityUseCase.togglePostLike(postId, currentUuid, isLike)
                .catch { e ->
                    Log.e("CommunityPostViewModel", "Error toggling like for post $postId", e)
                    if (currentState is Resource.Success && currentState.data != null) {
                        _post.value = currentState
                    }
                }
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            Log.d("CommunityPostViewModel", "Like toggled successfully for post $postId. UI updated optimistically.")
                        }
                        is Resource.Error -> {
                            Log.e("CommunityPostViewModel", "Failed to toggle like: ${resource.message}")
                        }
                        is Resource.Loading -> {}
                    }
                }
        }
    }

    fun clearReplyTo() {
        _replyToCommentId.value = null
        _replyToAuthorName.value = null
    }
}