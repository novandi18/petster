package com.novandiramadhan.petster.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.datastore.AuthDataStore
import com.novandiramadhan.petster.domain.model.AuthState
import com.novandiramadhan.petster.domain.model.PetHome
import com.novandiramadhan.petster.domain.model.VolunteerDashboardResult
import com.novandiramadhan.petster.domain.usecase.PetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val petUseCase: PetUseCase
): ViewModel() {
    private val _authState: MutableStateFlow<AuthState?> = MutableStateFlow(null)
    val authState: StateFlow<AuthState?> = _authState.asStateFlow()

    private val _pets = MutableStateFlow<Resource<PetHome>?>(null)
    val pets: StateFlow<Resource<PetHome>?> = _pets.asStateFlow()

    private val _volunteerDashboard = MutableStateFlow<Resource<VolunteerDashboardResult>?>(null)
    val volunteerDashboard: StateFlow<Resource<VolunteerDashboardResult>?> = _volunteerDashboard.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        getUserLoggedIn()
    }

    fun getUserLoggedIn() {
        viewModelScope.launch {
            authDataStore.state
                .onStart {
                    Log.d("HomeViewModel", "Observing Auth State...")
                }
                .catch { exception ->
                    Log.e("HomeViewModel", "Error observing Auth State", exception)
                    _authState.value = AuthState(userType = UserType.NONE)
                    reset()
                }
                .collect { result ->
                    _authState.value = result
                    when (result.userType) {
                        UserType.SHELTER -> {
                            getPets(result.uuid.toString())
                        }
                        UserType.VOLUNTEER -> {
                            getVolunteerDashboardData(result.uuid.toString())
                        }
                        UserType.NONE -> reset()
                    }
                }
        }
    }

    fun getPets(uuid: String) {
        viewModelScope.launch {
            petUseCase.getPetsHome(
                limitEachCategory = 10,
                shelterId = uuid
            ).catch { e ->
                _pets.value = Resource.Error(e.message ?: "Unknown error occurred")
                _isRefreshing.value = false
            }.collect { result ->
                _pets.value = result
                if (result is Resource.Success) _isRefreshing.value = false
            }
        }
    }

    fun getVolunteerDashboardData(volunteerId: String) {
        viewModelScope.launch {
            petUseCase.getVolunteerDashboard(volunteerId)
                .catch { e ->
                    _volunteerDashboard.value = Resource.Error(e.message ?: "Unknown error occurred")
                    _isRefreshing.value = false
                }.collect { result ->
                    _volunteerDashboard.value = result
                    if (result is Resource.Success) _isRefreshing.value = false
                }
        }
    }

    fun refresh() {
        _isRefreshing.value = true
        when (authState.value?.userType) {
            UserType.SHELTER -> {
                getPets(authState.value!!.uuid.toString())
            }
            UserType.VOLUNTEER -> {
                getVolunteerDashboardData(authState.value!!.uuid.toString())
            }
            UserType.NONE -> _isRefreshing.value = false
            null -> _isRefreshing.value = false
        }
    }

    fun reset() {
        _pets.value = null
        _volunteerDashboard.value = null
    }
}