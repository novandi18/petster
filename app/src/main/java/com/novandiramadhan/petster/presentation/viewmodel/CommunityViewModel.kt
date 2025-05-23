package com.novandiramadhan.petster.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.datastore.AuthDataStore
import com.novandiramadhan.petster.domain.model.AuthState
import com.novandiramadhan.petster.domain.model.PostResult
import com.novandiramadhan.petster.domain.usecase.CommunityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val communityUseCase: CommunityUseCase
): ViewModel() {
    private val _authState: MutableStateFlow<AuthState?> = MutableStateFlow(null)
    val authState: StateFlow<AuthState?> = _authState.asStateFlow()

    private val _likedPostsMap = MutableStateFlow<Map<String, Boolean>>(mapOf())
    val likedPostsMap: StateFlow<Map<String, Boolean>> = _likedPostsMap.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val communityPosts: Flow<PagingData<PostResult>> = authState.flatMapLatest { state ->
        if (state?.userType != UserType.NONE && !state?.uuid.isNullOrEmpty()) {
            initializePostsFlow(state.uuid)
        } else {
            flowOf(PagingData.empty())
        }
    }.cachedIn(viewModelScope)

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

    fun toggleLike(postId: String, isLike: Boolean) {
        viewModelScope.launch {
            val currentUuid = _authState.value?.uuid
            if (currentUuid == null) {
                Log.e("CommunityViewModel", "Cannot toggle like, user UUID is null")
                return@launch
            }

            _likedPostsMap.value = _likedPostsMap.value.toMutableMap().apply {
                put(postId, isLike)
            }

            communityUseCase.togglePostLike(postId, currentUuid, isLike)
                .catch { e ->
                    Log.e("CommunityViewModel", "Error toggling like for post $postId", e)
                    _likedPostsMap.value = _likedPostsMap.value.toMutableMap().apply {
                        remove(postId)
                    }
                }
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            Log.d("CommunityViewModel", "Like toggled successfully for post $postId")
                        }
                        is Resource.Error -> {
                            Log.e("CommunityViewModel", "Failed to toggle like: ${resource.message}")
                            _likedPostsMap.value = _likedPostsMap.value.toMutableMap().apply {
                                remove(postId)
                            }
                        }
                        is Resource.Loading -> {}
                    }
                }
        }
    }

    private fun initializePostsFlow(uuid: String): Flow<PagingData<PostResult>> {
        return communityUseCase.getPosts(uuid)
    }
}