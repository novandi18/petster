package com.novandiramadhan.petster.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.datastore.AuthDataStore
import com.novandiramadhan.petster.domain.model.AuthState
import com.novandiramadhan.petster.domain.model.ShelterAuthResult
import com.novandiramadhan.petster.domain.model.ShelterForm
import com.novandiramadhan.petster.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShelterConnectViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val authDataStore: AuthDataStore
): ViewModel() {
    private val _authState = MutableStateFlow<Resource<ShelterAuthResult>?>(null)
    val authState: StateFlow<Resource<ShelterAuthResult>?> = _authState.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            authUseCase.loginShelter(email, password)
                .catch { err ->
                    _authState.value = Resource.Error(err.message.toString())
                }
                .collect { result ->
                    _authState.value = result
                }
        }
    }

    fun signUp(shelterForm: ShelterForm) {
        viewModelScope.launch {
            authUseCase.registerShelter(shelterForm)
                .catch { err ->
                    _authState.value = Resource.Error(err.message.toString())
                }
                .collect { result ->
                    _authState.value = result
                }
        }
    }

    fun saveAuthData(authState: AuthState) {
        viewModelScope.launch {
            authDataStore.setAuthState(authState)
        }
    }

    fun resetAuthState() {
        _authState.value = null
    }
}