package com.novandiramadhan.petster.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.datastore.AuthDataStore
import com.novandiramadhan.petster.domain.model.AuthState
import com.novandiramadhan.petster.domain.model.Shelter
import com.novandiramadhan.petster.domain.model.ShelterForm
import com.novandiramadhan.petster.domain.model.UserResult
import com.novandiramadhan.petster.domain.model.Volunteer
import com.novandiramadhan.petster.domain.model.VolunteerForm
import com.novandiramadhan.petster.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val  authDataStore: AuthDataStore
): ViewModel() {
    private val _authState: MutableStateFlow<AuthState?> = MutableStateFlow(null)
    val authState: StateFlow<AuthState?> = _authState.asStateFlow()

    private val _user = MutableStateFlow<Resource<UserResult>>(Resource.Loading())
    val user: StateFlow<Resource<UserResult>> = _user.asStateFlow()

    private val _updateProfileState = MutableStateFlow<Resource<Unit>?>(null)
    val updateProfileState: StateFlow<Resource<Unit>?> = _updateProfileState.asStateFlow()

    private val _isFormDisabled = MutableStateFlow(false)
    val isFormDisabled: StateFlow<Boolean> = _isFormDisabled.asStateFlow()

    val volunteerProfile: StateFlow<Volunteer?> = user.mapState { resource ->
        (resource as? Resource.Success)?.data?.let { userResult ->
            (userResult as? UserResult.VolunteerResult)?.volunteer
        }
    }

    val shelterProfile: StateFlow<Shelter?> = user.mapState { resource ->
        (resource as? Resource.Success)?.data?.let { userResult ->
            (userResult as? UserResult.ShelterResult)?.shelter
        }
    }

    init {
        getUserLoggedIn()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserLoggedIn() {
        viewModelScope.launch {
            authDataStore.state
                .onStart {
                    Log.d("ProfileViewModel", "Observing Auth State...")
                }
                .catch { exception ->
                    Log.e("ProfileViewModel", "Error observing Auth State", exception)
                    _authState.value = AuthState(userType = UserType.NONE)
                    _user.value = Resource.Error("Failed to get authentication state: ${exception.message}")
                }
                .flatMapLatest { state: AuthState ->
                    _authState.value = state
                    Log.d("ProfileViewModel", "Auth State Collected: $state")

                    if (state.uuid != null && state.uuid.isNotBlank() && state.userType != UserType.NONE) {
                        Log.d("ProfileViewModel", "Fetching profile for UUID: ${state.uuid}, Type: ${state.userType}")
                        authUseCase.getUser(uuid = state.uuid, userType = state.userType)
                            .onStart {
                                _user.value = Resource.Loading()
                            }
                            .catch { fetchError ->
                                Log.e("ProfileViewModel", "Error fetching profile for ${state.uuid}", fetchError)
                                emit(Resource.Error(fetchError.message ?: "Unknown error fetching profile"))
                            }
                    } else {
                        flowOf(Resource.Error("User not logged in or user type unknown."))
                    }
                }
                .collect { profileResult ->
                    Log.d("ProfileViewModel", "Profile Result Collected: ${profileResult::class.java.simpleName}")
                    _user.value = profileResult
                }
        }
    }

    fun refreshUserProfile() {
        val currentState = _authState.value
        if (currentState?.uuid != null && currentState.uuid.isNotBlank() && currentState.userType != UserType.NONE) {
            _user.value = Resource.Loading()
            viewModelScope.launch {
                authUseCase.getUser(currentState.uuid, currentState.userType)
                    .catch { fetchError ->
                        Log.e("ProfileViewModel", "Error refreshing profile for ${currentState.uuid}", fetchError)
                        emit(Resource.Error(
                            message = fetchError.message ?: "Unknown error refreshing profile")
                        )
                    }
                    .collect { profileResult ->
                        _user.value = profileResult
                    }
            }
        } else {
            _user.value = Resource.Error("Cannot refresh: User not logged in.")
        }
    }

    fun logout() {
        viewModelScope.launch {
            authDataStore.deleteAuthState()
            _user.value = Resource.Error("User logged out.")
        }
    }

    fun updateVolunteerProfile(form: VolunteerForm, uuid: String) {
        viewModelScope.launch {
            _isFormDisabled.value = true
            authUseCase.updateVolunteer(form, uuid)
                .catch { updateError ->
                    Log.e("ProfileViewModel", "Error updating profile for $uuid", updateError)
                    _updateProfileState.value = Resource.Error(
                        message = updateError.message ?: "Unknown error updating profile"
                    )
                    _isFormDisabled.value = false
                }
                .collect { updateResult ->
                    Log.d("ProfileViewModel", "Update Result Collected: ${updateResult::class.java.simpleName}")
                    _updateProfileState.value = updateResult
                    if (updateResult !is Resource.Loading) {
                        _isFormDisabled.value = false
                    }
                }
        }
    }

    fun updateShelterProfile(form: ShelterForm, uuid: String) {
        viewModelScope.launch {
            _isFormDisabled.value = true
            authUseCase.updateShelter(form, uuid)
                .catch { updateError ->
                    Log.e("ProfileViewModel", "Error updating profile for $uuid", updateError)
                    _updateProfileState.value = Resource.Error(
                        message = updateError.message ?: "Unknown error updating profile"
                    )
                    _isFormDisabled.value = false
                }
                .collect { updateResult ->
                    Log.d("ProfileViewModel", "Update Result Collected: ${updateResult::class.java.simpleName}")
                    _updateProfileState.value = updateResult
                    if (updateResult !is Resource.Loading) {
                        _isFormDisabled.value = false
                    }
                }
        }
    }

    private fun <T> StateFlow<Resource<UserResult>>.mapState(mapper: (Resource<UserResult>) -> T?): StateFlow<T?> {
        return this.map(mapper).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = mapper(this.value)
        )
    }

    fun resetUpdateProfileState() {
        _updateProfileState.value = null
    }
}