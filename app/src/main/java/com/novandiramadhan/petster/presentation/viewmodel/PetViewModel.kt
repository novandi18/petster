package com.novandiramadhan.petster.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novandiramadhan.petster.common.FirebaseKeys
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.datastore.AuthDataStore
import com.novandiramadhan.petster.domain.model.AuthState
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.domain.model.PetView
import com.novandiramadhan.petster.domain.model.UserResult
import com.novandiramadhan.petster.domain.usecase.AuthUseCase
import com.novandiramadhan.petster.domain.usecase.PetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.novandiramadhan.petster.domain.model.Result
import com.novandiramadhan.petster.domain.usecase.FavoritePetUseCase

@HiltViewModel
class PetViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val petUseCase: PetUseCase,
    private val authUseCase: AuthUseCase,
    private val favoritePetUseCase: FavoritePetUseCase
): ViewModel() {
    private val _authState: MutableStateFlow<AuthState?> = MutableStateFlow(null)
    val authState: StateFlow<AuthState?> = _authState.asStateFlow()

    private val _pet: MutableStateFlow<Resource<Pet>?> = MutableStateFlow(null)
    val pet: StateFlow<Resource<Pet>?> = _pet.asStateFlow()

    private val _volunteer: MutableStateFlow<Resource<UserResult>?> = MutableStateFlow(null)
    val volunteer: StateFlow<Resource<UserResult>?> = _volunteer.asStateFlow()

    private val _shelterLoggedIn: MutableStateFlow<Resource<UserResult>?> = MutableStateFlow(null)
    val shelterLoggedIn: StateFlow<Resource<UserResult>?> = _shelterLoggedIn.asStateFlow()

    private val _selectedImageIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val selectedImageIndex: StateFlow<Int> = _selectedImageIndex.asStateFlow()

    private val _showDeleteConfirmation = MutableStateFlow(false)
    val showDeleteConfirmation: StateFlow<Boolean> = _showDeleteConfirmation.asStateFlow()

    private val _deleteState = MutableStateFlow<Resource<Result>?>(null)
    val deleteState: StateFlow<Resource<Result>?> = _deleteState.asStateFlow()

    private val _petFavStatus: MutableStateFlow<Resource<Result>?> = MutableStateFlow(null)
    val petFavStatus: StateFlow<Resource<Result>?> = _petFavStatus.asStateFlow()

    private val _adoptedToggleState = MutableStateFlow<Resource<Result>?>(null)
    val adoptedToggleState: StateFlow<Resource<Result>?> = _adoptedToggleState.asStateFlow()

    init {
        getUserLoggedIn()
    }

    fun selectImage(index: Int) {
        _selectedImageIndex.value = index
    }

    fun setShowDeleteConfirmation(show: Boolean) {
        _showDeleteConfirmation.value = show
    }

    fun getUserLoggedIn() {
        viewModelScope.launch {
            authDataStore.state
                .onStart {
                    Log.d("PetViewModel", "Observing Auth State...")
                }
                .catch { exception ->
                    Log.e("PetViewModel", "Error observing Auth State", exception)
                    _authState.value = AuthState(userType = UserType.NONE)
                }
                .collect { result ->
                    _authState.value = result
                    if (result.userType == UserType.SHELTER && result.uuid != null) {
                        getShelterLoggedIn(result.uuid)
                    }
                }
        }
    }

    fun getPet(petId: String) {
        viewModelScope.launch {
            petUseCase.getPetById(
                id = petId,
                shelterId = if (authState.value?.userType == UserType.SHELTER) {
                    authState.value!!.uuid
                } else null)
                .catch { exception ->
                    Log.e("PetViewModel", "Error observing Pet", exception)
                    _pet.value = Resource.Error(exception.message.toString())
                }
                .collect { result ->
                    _pet.value = result
                    if (result is Resource.Success) {
                        result.data?.volunteer?.let { volunteer ->
                            val volunteerId = volunteer.replace("${FirebaseKeys.VOLUNTEER_COLLECTION}/", "")
                            getVolunteer(volunteerId)
                        }

                        result.data?.image?.let { image ->
                            val coverUrl = image.imageCoverUrl
                            if (coverUrl != null && image.imageUrls != null) {
                                val coverIndex = image.imageUrls.indexOf(coverUrl)
                                if (coverIndex >= 0) {
                                    _selectedImageIndex.value = coverIndex
                                }
                            }
                        }
                    }
                }
        }
    }

    fun getVolunteer(volunteerId: String) {
        viewModelScope.launch {
            authUseCase.getUser(volunteerId, UserType.VOLUNTEER)
                .catch { exception ->
                    Log.e("PetViewModel", "Error observing Volunteer", exception)
                    _volunteer.value = Resource.Error(exception.message.toString())
                }
                .collect { result ->
                    _volunteer.value = result
                }
        }
    }

    fun getShelterLoggedIn(uuid: String) {
        viewModelScope.launch {
            authUseCase.getUser(uuid, UserType.SHELTER)
                .catch { exception ->
                    Log.e("PetViewModel", "Error observing Volunteer", exception)
                    _shelterLoggedIn.value = Resource.Error(exception.message.toString())
                }
                .collect { result ->
                    _shelterLoggedIn.value = result
                }
        }
    }

    fun deletePet() {
        val petData = _pet.value
        if (petData is Resource.Success && petData.data != null) {
            val petId = petData.data.id ?: return

            viewModelScope.launch {
                _deleteState.value = Resource.Loading()

                petUseCase.deletePet(petId)
                    .catch { exception ->
                        Log.e("PetViewModel", "Error deleting pet", exception)
                        _deleteState.value = Resource.Error(exception.message.toString())
                    }
                    .collect { result ->
                        _deleteState.value = result
                    }
            }
        }
    }

    fun addViewedPet(petId: String) {
        viewModelScope.launch {
            if (authState.value?.userType != UserType.SHELTER || authState.value?.uuid.isNullOrBlank()) {
                return@launch
            }

            val shelterId = authState.value?.uuid ?: return@launch
            val petView = PetView(
                petId = petId,
                shelterId = shelterId
            )

            petUseCase.addViewedPet(petView)
                .catch { exception ->
                    Log.e("PetViewModel", "Error recording pet view", exception)
                }
                .collect {}
        }
    }

    fun togglePetFavorite(petId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            favoritePetUseCase.toggleFavoritePet(
                petId = petId,
                shelterId = _authState.value?.uuid ?: "",
                isFavorite = isFavorite
            ).catch { exception ->
                Log.e("PetViewModel", "Error toggle Pet to Favorites", exception)
                _petFavStatus.value = Resource.Error(exception.message ?: "Error toggle to favorites")
            }.collect { result ->
                _petFavStatus.value = result

                if (result is Resource.Success) {
                    _pet.value?.let { currentPet ->
                        if (currentPet is Resource.Success && currentPet.data != null) {
                            _pet.value = Resource.Success(currentPet.data.copy(isFavorite = isFavorite))
                        }
                    }
                }
            }
        }
    }

    fun togglePetAdopted(petId: String, isAdopted: Boolean) {
        viewModelScope.launch {
            _adoptedToggleState.value = Resource.Loading()

            petUseCase.togglePetAdopted(petId, isAdopted)
                .catch { exception ->
                    Log.e("PetViewModel", "Error toggling pet adoption status", exception)
                    _adoptedToggleState.value = Resource.Error(exception.message ?: "Error updating adoption status")
                }
                .collect { result ->
                    _adoptedToggleState.value = result

                    if (result is Resource.Success) {
                        _pet.value?.let { currentPet ->
                            if (currentPet is Resource.Success && currentPet.data != null) {
                                _pet.value = Resource.Success(currentPet.data.copy(isAdopted = isAdopted))
                            }
                        }
                    }
                }
        }
    }

    fun retry() {
        _pet.value?.let { resource ->
            if (resource is Resource.Error) {
                resource.message?.let { message ->
                    getPet(message)
                }
            }
        }
    }

    fun resetDeleteState() {
        _deleteState.value = null
    }

    fun resetPetFavStatus() {
        _petFavStatus.value = null
    }

    fun resetAdoptedToggleState() {
        _adoptedToggleState.value = null
    }
}