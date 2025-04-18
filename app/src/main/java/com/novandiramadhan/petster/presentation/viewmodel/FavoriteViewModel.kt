package com.novandiramadhan.petster.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.datastore.AuthDataStore
import com.novandiramadhan.petster.domain.model.AuthState
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.domain.usecase.FavoritePetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val favoritePetUseCase: FavoritePetUseCase
): ViewModel() {
    private val _authState: MutableStateFlow<AuthState?> = MutableStateFlow(null)
    val authState: StateFlow<AuthState?> = _authState.asStateFlow()

    private val _pets = MutableStateFlow<Resource<List<Pet>>?>(null)
    val pets: StateFlow<Resource<List<Pet>>?> = _pets.asStateFlow()

    init {
        getUserLoggedIn()
    }

    fun getUserLoggedIn() {
        viewModelScope.launch {
            authDataStore.state
                .onStart {
                    Log.d("ProfileViewModel", "Observing Auth State...")
                }
                .catch { exception ->
                    Log.e("ProfileViewModel", "Error observing Auth State", exception)
                    _authState.value = AuthState(userType = UserType.NONE)
                    resetFavoritePets()
                }
                .collect { result ->
                    _authState.value = result
                    if (result.userType != UserType.SHELTER) {
                        resetFavoritePets()
                    } else {
                        getFavoritePets(result.uuid.toString())
                    }
                }
        }
    }

    fun getFavoritePets(shelterId: String) {
        viewModelScope.launch {
            favoritePetUseCase.getFavoritePets(shelterId)
                .catch { e ->
                    _pets.value = Resource.Error(e.message.toString())
                }
                .collect { result ->
                    _pets.value = result
                }
        }
    }

    fun removeFavorite(petId: String) {
        val shelterId = authState.value?.uuid.toString()

        viewModelScope.launch {
            favoritePetUseCase.toggleFavoritePet(petId, shelterId, false)
                .catch { e ->
                    Log.e("FavoriteViewModel", "Error removing favorite: ${e.message}")
                }
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            getFavoritePets(shelterId)
                        }
                        is Resource.Error -> {
                            Log.e("FavoriteViewModel", "Failed to remove favorite: ${result.message}")
                        }
                        else -> {}
                    }
                }
        }
    }

    fun retry() {
        _pets.value = null
        getFavoritePets(authState.value?.uuid.toString())
    }

    fun resetFavoritePets() {
        _pets.value = null
    }
}