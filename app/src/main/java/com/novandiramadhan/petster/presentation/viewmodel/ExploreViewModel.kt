package com.novandiramadhan.petster.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.novandiramadhan.petster.common.LocationFilterParams
import com.novandiramadhan.petster.common.states.PetFilterState
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.datastore.AuthDataStore
import com.novandiramadhan.petster.domain.model.AuthState
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.domain.model.Result
import com.novandiramadhan.petster.domain.model.ShelterLocation
import com.novandiramadhan.petster.domain.usecase.FavoritePetUseCase
import com.novandiramadhan.petster.domain.usecase.PetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val petUseCase: PetUseCase,
    private val favoritePetUseCase: FavoritePetUseCase
): ViewModel() {
    private val _authState: MutableStateFlow<AuthState?> = MutableStateFlow(null)
    val authState: StateFlow<AuthState?> = _authState.asStateFlow()

    private val _petFavStatus: MutableStateFlow<Resource<Result>?> = MutableStateFlow(null)
    val petFavStatus: StateFlow<Resource<Result>?> = _petFavStatus.asStateFlow()

    private val _updatedPetId = MutableStateFlow<String?>(null)
    val updatedPetId = _updatedPetId.asStateFlow()

    private val _updatedPetFavorites = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val updatedPetFavorites = _updatedPetFavorites.asStateFlow()

    private val _filterState = MutableStateFlow(PetFilterState())
    val filterState = _filterState.asStateFlow()

    private val _isLocationFilterActive = MutableStateFlow(false)
    val isLocationFilterActive = _isLocationFilterActive.asStateFlow()

    private val _userLocation = MutableStateFlow<ShelterLocation?>(null)
    val userLocation = _userLocation.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val pets: Flow<PagingData<Pet>> = combine(authState, filterState, isLocationFilterActive, userLocation) {
            state, filter, locationActive, location ->
        LocationFilterParams(state, filter, locationActive, location)
    }.flatMapLatest { params ->
        if (!params.state?.uuid.isNullOrEmpty()) {
            initializePetsFlow(
                shelterId = params.state.uuid,
                filter = params.filter,
                useLocation = params.locationActive,
                location = params.location,
            )
        } else {
            initializePetsFlow(
                filter = params.filter,
                useLocation = params.locationActive,
                location = params.location,
            )
        }
    }.cachedIn(viewModelScope)

    init {
        getUserLoggedIn()
    }

    fun toggleLocationFilter(isActive: Boolean, location: ShelterLocation? = null) {
        _isLocationFilterActive.value = isActive
        if (location != null) {
            _userLocation.value = location
        }
    }

    private fun initializePetsFlow(
        shelterId: String? = null,
        filter: PetFilterState? = null,
        useLocation: Boolean = false,
        location: ShelterLocation? = null,
    ): Flow<PagingData<Pet>> {
        return if (useLocation && location != null) {
            petUseCase.getPets(shelterId, filter, location)
        } else {
            petUseCase.getPets(shelterId, filter)
        }
    }

    fun getUserLoggedIn() {
        viewModelScope.launch {
            authDataStore.state
                .onStart {
                    Log.d("ExploreViewModel", "Observing Auth State...")
                }
                .catch { exception ->
                    Log.e("ExploreViewModel", "Error observing Auth State", exception)
                    _authState.value = AuthState(userType = UserType.NONE)
                }
                .collect { result ->
                    _authState.value = result
                }
        }
    }

    fun togglePetFavorite(petId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            favoritePetUseCase.toggleFavoritePet(
                petId = petId,
                shelterId = _authState.value?.uuid ?: "",
                isFavorite = isFavorite
            ).catch { exception ->
                Log.e("ExploreViewModel", "Error toggle Pet to Favorites", exception)
                _petFavStatus.value = Resource.Error(exception.message ?: "Error toggle to favorites")
            }.collect { result ->
                _petFavStatus.value = result
                if (result is Resource.Success) {
                    val currentMap = _updatedPetFavorites.value.toMutableMap()
                    currentMap[petId] = isFavorite
                    _updatedPetFavorites.value = currentMap

                    _updatedPetId.value = petId
                    viewModelScope.launch {
                        delay(300)
                        _updatedPetId.value = null
                    }
                }
            }
        }
    }

    fun updateFilters(newFilters: PetFilterState) {
        _filterState.value = newFilters
    }

    fun resetAddFavStatus() {
        _petFavStatus.value = null
    }
}