package com.novandiramadhan.petster.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mr0xf00.easycrop.ImageCropper
import com.novandiramadhan.petster.common.FirebaseKeys
import com.novandiramadhan.petster.common.states.PetPhotoModalState
import com.novandiramadhan.petster.common.states.PetPhotoState
import com.novandiramadhan.petster.common.states.PetUriState
import com.novandiramadhan.petster.common.states.UploadState
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.common.utils.formatIbbUrl
import com.novandiramadhan.petster.data.remote.request.ImgBBRequest
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.datastore.AuthDataStore
import com.novandiramadhan.petster.domain.model.AuthState
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.domain.model.PetImage
import com.novandiramadhan.petster.domain.usecase.PetImageUseCase
import com.novandiramadhan.petster.domain.usecase.PetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.toString

@HiltViewModel
class NewPetViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val petUseCase: PetUseCase,
    private val petImageUseCase: PetImageUseCase
) : ViewModel() {
    private val _authState: MutableStateFlow<AuthState?> = MutableStateFlow(null)
    val authState: StateFlow<AuthState?> = _authState.asStateFlow()

    val imageCropper = ImageCropper()

    private val _photoUris = MutableStateFlow<List<PetUriState>>(emptyList())
    val photoUris: StateFlow<List<PetUriState>> = _photoUris

    private val _pet = MutableStateFlow(Pet())
    val pet: StateFlow<Pet> = _pet

    private val _petImages = MutableStateFlow(PetImage())

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    private val _photoModal = MutableStateFlow(PetPhotoModalState())
    val photoModal: StateFlow<PetPhotoModalState> = _photoModal

    private val _isFieldsValid = MutableStateFlow(false)
    val isFieldsValid: StateFlow<Boolean> = _isFieldsValid

    init {
        getUserLoggedIn()
    }

    fun getUserLoggedIn() {
        viewModelScope.launch {
            authDataStore.state
                .onStart {
                    Log.d("NewPetViewModel", "Observing Auth State...")
                }
                .catch { exception ->
                    Log.e("NewPetViewModel", "Error observing Auth State", exception)
                    _authState.value = AuthState(userType = UserType.NONE)
                }
                .collect { result ->
                    _authState.value = result
                    _pet.value = _pet.value.copy(
                        volunteer = "${FirebaseKeys.VOLUNTEER_COLLECTION}/${result.uuid}",
                    )
                }
        }
    }

    fun addPhoto(base64Data: String, displayUri: String) {
        if (_photoUris.value.size < 5) {
            val petUri = if (_photoUris.value.isEmpty()) {
                PetUriState(uri = displayUri, base64Data = base64Data, isCover = true)
            } else {
                PetUriState(uri = displayUri, base64Data = base64Data)
            }
            _photoUris.value += petUri
            updateFieldsValidity()
        }
    }

    fun removePhoto(index: Int) {
        if (index in _photoUris.value.indices) {
            val newPhotoUris = _photoUris.value.toMutableList()

            if (newPhotoUris[index].isCover) {
                newPhotoUris.firstOrNull { !it.isCover }?.let {
                    it.isCover = true
                }
            }

            newPhotoUris.removeAt(index)
            _photoUris.value = newPhotoUris
            updateFieldsValidity()

            if (_photoModal.value.photoUriIndex == index) {
                _photoModal.value = _photoModal.value.copy(
                    photoUriIndex = -1,
                )
            }
        }
    }

    fun updatePhotoUri(index: Int, newUri: String, newBase64Data: String) {
        if (index in _photoUris.value.indices) {
            val updatedUris = _photoUris.value.toMutableList()

            updatedUris[index] = updatedUris[index].copy(
                uri = newUri,
                base64Data = newBase64Data
            )

            _photoUris.value = updatedUris
        }
        Log.d("CHANGE PHOTO", _photoUris.value.toString())
    }

    fun setPetName(name: String) {
        _pet.value = _pet.value.copy(name = name)
        updateFieldsValidity()
    }

    fun setPetAge(age: String) {
        _pet.value = _pet.value.copy(
            age = if (age.isNotEmpty()) age.toIntOrNull() else null
        )
        updateFieldsValidity()
    }

    fun setPetAgeUnit(ageUnit: String) {
        _pet.value = _pet.value.copy(ageUnit = ageUnit)
    }

    fun setPetCategory(category: String) {
        _pet.value = _pet.value.copy(category = category)
    }

    fun setPetGender(gender: String) {
        _pet.value = _pet.value.copy(gender = gender)
    }

    fun setPetColor(color: String) {
        _pet.value = _pet.value.copy(color = color)
        updateFieldsValidity()
    }

    fun setPetBreed(breed: String) {
        _pet.value = _pet.value.copy(breed = breed)
    }

    fun setPetSize(size: String) {
        _pet.value = _pet.value.copy(size = size)
    }

    fun setPetWeight(weight: String) {
        _pet.value = _pet.value.copy(
            weight = if (weight.isNotEmpty()) weight else null
        )
        updateFieldsValidity()
    }

    fun setPetWeightUnit(weightUnit: String) {
        _pet.value = _pet.value.copy(weightUnit = weightUnit)
    }

    fun setPetDisabilities(disabilities: List<String>) {
        _pet.value = _pet.value.copy(disabilities = disabilities)
    }

    fun setPetVaccinated(vaccinated: String) {
        val isVaccinated = vaccinated.equals("Yes", ignoreCase = true)
        _pet.value = _pet.value.copy(isVaccinated = isVaccinated)
    }

    fun setPetBehaviors(behaviors: List<String>) {
        _pet.value = _pet.value.copy(behaviours = behaviors)
    }

    fun setPetAdoptFee(fee: String) {
        _pet.value = _pet.value.copy(adoptionFee = fee.toIntOrNull())
    }

    fun toggleModal(isShowed: Boolean) {
        _photoModal.value = _photoModal.value.copy(
            isShowed = isShowed,
        )
    }

    fun setPhotoModal(index: Int, isCover: Boolean) {
        _photoModal.value = _photoModal.value.copy(
            photoUriIndex = index,
            isPhotoCover = isCover
        )
    }

    fun setModalState(state: PetPhotoState) {
        _photoModal.value = _photoModal.value.copy(state = state)
    }

    fun setCoverPhoto(index: Int) {
        _photoUris.value = _photoUris.value.mapIndexed { i, petUri ->
            petUri.copy(isCover = i == index)
        }
    }

    fun resetPhotoModal() {
        _photoModal.value = PetPhotoModalState()
    }

    fun uploadImagesAndPostPet() {
        viewModelScope.launch {
            try {
                _uploadState.value = UploadState.Loading
                val imagesList = _photoUris.value

                Log.d("IMAGE_UPLOAD", "Starting upload of ${imagesList.size} images")
                uploadImagesRecursively(imagesList, 0)
            } catch (e: Exception) {
                Log.e("IMAGE_UPLOAD", "Upload failed: ${e.message}")
                _uploadState.value = UploadState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun uploadImagesRecursively(
        images: List<PetUriState>,
        currentIndex: Int
    ) {
        if (currentIndex >= images.size) {
            Log.d("Uploaded Urls", _petImages.value.toString())
            postPet()
            return
        }

        viewModelScope.launch {
            try {
                Log.d("POST NEW PET: STEP 1", "Uploading image ${currentIndex+1}/${images.size}")
                val imgRequest = ImgBBRequest(image = images[currentIndex].base64Data)

                petImageUseCase.uploadImage(imgRequest)
                    .collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                result.data?.let { response ->
                                    Log.d("POST NEW PET: STEP 1", "Success for image ${currentIndex+1}")

                                    if (images[currentIndex].isCover) {
                                        _petImages.value = _petImages.value.copy(
                                            imageCoverUrl = formatIbbUrl(response.url)
                                        )
                                    }

                                    _petImages.value = _petImages.value.copy(
                                        imageUrls = _petImages.value.imageUrls
                                            ?.plus(formatIbbUrl(response.url)) ?: listOf(formatIbbUrl(response.url))
                                    )

                                    uploadImagesRecursively(images, currentIndex + 1)
                                }
                            }
                            is Resource.Error -> {
                                Log.e("POST NEW PET: STEP 1", "Error for image ${currentIndex+1}: ${result.message}")
                                _uploadState.value = UploadState.Error(result.message ?: "Unknown error")
                            }
                            is Resource.Loading -> {}
                        }
                    }
            } catch (e: Exception) {
                Log.e("POST NEW PET: STEP 1", "Failed to upload image ${currentIndex+1}: ${e.message}")
                _uploadState.value = UploadState.Error("Failed to upload image ${currentIndex+1}: ${e.message}")
            }
        }
    }

    private fun postPet() {
        _pet.value = _pet.value.copy(image = null)

        viewModelScope.launch {
            petUseCase.addPet(_pet.value.copy(image = _petImages.value))
                .catch {
                    _uploadState.value = UploadState.Error(it.message.toString())
                }
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            Log.d("POST NEW PET", "Pet created successfully: ${result.data?.message}")
                            _uploadState.value = UploadState.Success
                        }
                        is Resource.Error -> {
                            Log.d("POST NEW PET", "Error creating pet: ${result.message}")
                            _uploadState.value = UploadState.Error(result.message.toString())
                        }
                        is Resource.Loading -> {}
                    }
                }
        }
    }

    fun setUploadState(state: UploadState) {
        _uploadState.value = state
    }

    fun resetState() {
        _photoUris.value = emptyList()
        _pet.value = Pet()
        _uploadState.value = UploadState.Idle
    }

    private fun updateFieldsValidity() {
        _isFieldsValid.value = _photoUris.value.isNotEmpty() && _pet.value.name?.isNotEmpty() == true
                && _pet.value.color?.isNotEmpty() == true && _pet.value.weight != null &&
                _pet.value.age != null
    }
}