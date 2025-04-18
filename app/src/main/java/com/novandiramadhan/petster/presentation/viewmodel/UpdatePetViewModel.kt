package com.novandiramadhan.petster.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mr0xf00.easycrop.ImageCropper
import com.novandiramadhan.petster.common.states.PetPhotoModalState
import com.novandiramadhan.petster.common.states.PetPhotoState
import com.novandiramadhan.petster.common.states.PetUriState
import com.novandiramadhan.petster.common.states.UploadState
import com.novandiramadhan.petster.common.utils.formatIbbUrl
import com.novandiramadhan.petster.data.remote.request.ImgBBRequest
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.domain.model.PetImage
import com.novandiramadhan.petster.domain.usecase.PetImageUseCase
import com.novandiramadhan.petster.domain.usecase.PetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatePetViewModel @Inject constructor(
    private val petUseCase: PetUseCase,
    private val petImageUseCase: PetImageUseCase
): ViewModel() {
    val imageCropper = ImageCropper()

    private val _updatedPet = MutableStateFlow<Pet?>(null)
    val updatedPet: StateFlow<Pet?> = _updatedPet.asStateFlow()

    private val _photoUris = MutableStateFlow<List<PetUriState>>(emptyList())
    val photoUris: StateFlow<List<PetUriState>> = _photoUris.asStateFlow()

    private val _pet = MutableStateFlow<Resource<Pet>?>(null)
    val pet: StateFlow<Resource<Pet>?> = _pet

    private val _photoModal = MutableStateFlow(PetPhotoModalState())
    val photoModal: StateFlow<PetPhotoModalState> = _photoModal

    private val _petImages = MutableStateFlow<PetImage?>(null)

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    private val _imagesChanged = MutableStateFlow(false)

    private val _isFieldsValid = MutableStateFlow(false)
    val isFieldsValid: StateFlow<Boolean> = _isFieldsValid

    fun getPet(petId: String) {
        viewModelScope.launch {
            petUseCase.getPetById(id = petId, shelterId = null)
                .catch { exception ->
                    Log.e("UpdatePetViewModel", "Error observing Pet", exception)
                    _pet.value = Resource.Error(exception.message.toString())
                }
                .collect { result ->
                    _pet.value = result
                    if (result is Resource.Success) {
                        _updatedPet.value = result.data
                        _petImages.value = result.data?.image
                        updateFieldsValidity()
                    }
                }
        }
    }

    fun setPetName(name: String) {
        _updatedPet.value = _updatedPet.value?.copy(name = name)
        updateFieldsValidity()
    }

    fun setPetAge(age: String) {
        age.toIntOrNull()?.let { ageValue ->
            _updatedPet.value = _updatedPet.value?.copy(age = ageValue)
        }
        updateFieldsValidity()
    }

    fun setPetAgeUnit(ageUnit: String) {
        _updatedPet.value = _updatedPet.value?.copy(ageUnit = ageUnit)
    }

    fun setPetCategory(category: String) {
        _updatedPet.value = _updatedPet.value?.copy(category = category)
    }

    fun setPetGender(gender: String) {
        _updatedPet.value = _updatedPet.value?.copy(gender = gender)
    }

    fun setPetColor(color: String) {
        _updatedPet.value = _updatedPet.value?.copy(color = color)
        updateFieldsValidity()
    }

    fun setPetBreed(breed: String) {
        _updatedPet.value = _updatedPet.value?.copy(breed = breed)
    }

    fun setPetSize(size: String) {
        _updatedPet.value = _updatedPet.value?.copy(size = size)
    }

    fun setPetWeight(weight: String) {
        _updatedPet.value = _updatedPet.value?.copy(weight = weight)
        updateFieldsValidity()
    }

    fun setPetWeightUnit(weightUnit: String) {
        _updatedPet.value = _updatedPet.value?.copy(weightUnit = weightUnit)
    }

    fun setPetDisabilities(disabilities: List<String>) {
        _updatedPet.value = _updatedPet.value?.copy(disabilities = disabilities)
    }

    fun setPetVaccinated(vaccinated: String) {
        val isVaccinated = vaccinated.equals("Yes", ignoreCase = true)
        _updatedPet.value = _updatedPet.value?.copy(isVaccinated = isVaccinated)
    }

    fun setPetBehaviors(behaviors: List<String>) {
        _updatedPet.value = _updatedPet.value?.copy(behaviours = behaviors)
    }

    fun setPetAdoptFee(fee: String) {
        _updatedPet.value = _updatedPet.value?.copy(adoptionFee = fee.toIntOrNull())
    }

    // Photo handling methods
    fun addPhoto(base64Data: String, displayUri: String) {
        if (_photoUris.value.size < 5) {
            val shouldBeCover = _photoUris.value.isEmpty() &&
                    (_petImages.value?.imageCoverUrl == null ||
                            _petImages.value?.imageUrls.isNullOrEmpty())

            val petUri = PetUriState(
                uri = displayUri,
                base64Data = base64Data,
                isCover = shouldBeCover
            )

            _photoUris.value += petUri
            _imagesChanged.value = true
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
            _imagesChanged.value = true
            updateFieldsValidity()

            if (_photoModal.value.photoUriIndex == index) {
                _photoModal.value = _photoModal.value.copy(
                    photoUriIndex = -1,
                )
            }
        }
    }

    fun removeUrlPhoto(urlIndex: Int) {
        val imageUrls = _petImages.value?.imageUrls ?: return
        if (urlIndex < imageUrls.size) {
            val newImageUrls = imageUrls.toMutableList().apply {
                removeAt(urlIndex)
            }

            val currentCover = _petImages.value?.imageCoverUrl
            val needsCoverUpdate = currentCover == imageUrls[urlIndex]

            _petImages.value = _petImages.value?.copy(
                imageUrls = newImageUrls,
                imageCoverUrl = if (needsCoverUpdate) {
                    newImageUrls.firstOrNull()
                } else {
                    currentCover
                }
            )

            _updatedPet.value = _updatedPet.value?.copy(image = _petImages.value)
            _imagesChanged.value = true
            updateFieldsValidity()
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
            _imagesChanged.value = true
        }
    }

    fun updateUrlPhoto(urlIndex: Int, newUri: String, newBase64Data: String) {
        val imageUrls = _petImages.value?.imageUrls?.toMutableList() ?: return

        if (urlIndex < imageUrls.size) {
            val wasCover = _petImages.value?.imageCoverUrl == _petImages.value?.imageUrls?.get(urlIndex)
            imageUrls.removeAt(urlIndex)

            _petImages.value = _petImages.value?.copy(
                imageUrls = imageUrls,
                imageCoverUrl = if (wasCover) null else _petImages.value?.imageCoverUrl
            )

            val newPhotoUri = PetUriState(
                uri = newUri,
                base64Data = newBase64Data,
                isCover = wasCover
            )

            _photoUris.value = _photoUris.value + newPhotoUri
            _updatedPet.value = _updatedPet.value?.copy(image = _petImages.value)
            _imagesChanged.value = true
        }
    }

    fun setUrlPhotoAsCover(urlIndex: Int) {
        val imageUrls = _petImages.value?.imageUrls ?: return
        if (urlIndex < imageUrls.size) {
            val newPetImages = _petImages.value?.copy(
                imageCoverUrl = imageUrls[urlIndex]
            )
            _petImages.value = newPetImages

            if (_photoUris.value.any { it.isCover }) {
                _photoUris.value = _photoUris.value.map { it.copy(isCover = false) }
            }

            _updatedPet.value = _updatedPet.value?.copy(image = newPetImages)
            _imagesChanged.value = true
        }
    }

    fun setCoverPhoto(index: Int) {
        _photoUris.value = _photoUris.value.mapIndexed { i, petUri ->
            petUri.copy(isCover = i == index)
        }

        var updatedPetImages = _petImages.value
        if (updatedPetImages?.imageCoverUrl != null) {
            updatedPetImages = updatedPetImages.copy(
                imageCoverUrl = null
            )
            _petImages.value = updatedPetImages
            _updatedPet.value = _updatedPet.value?.copy(image = updatedPetImages)
        }

        _imagesChanged.value = true
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

    fun resetPhotoModal() {
        _photoModal.value = PetPhotoModalState()
    }

    fun updatePet() {
        viewModelScope.launch {
            try {
                _uploadState.value = UploadState.Loading

                if (_imagesChanged.value && _photoUris.value.isNotEmpty()) {
                    Log.d("IMAGE_UPDATE", "Starting upload of ${_photoUris.value.size} images")
                    uploadImagesRecursively(_photoUris.value, 0)
                } else {
                    submitPetUpdate()
                }
            } catch (e: Exception) {
                Log.e("IMAGE_UPDATE", "Update failed: ${e.message}")
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
            submitPetUpdate()
            return
        }

        viewModelScope.launch {
            try {
                Log.d("UPDATE PET: STEP 1", "Uploading image ${currentIndex+1}/${images.size}")
                val imgRequest = ImgBBRequest(image = images[currentIndex].base64Data)

                petImageUseCase.uploadImage(imgRequest)
                    .collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                result.data?.let { response ->
                                    Log.d("UPDATE PET: STEP 1", "Success for image ${currentIndex+1}")

                                    if (_petImages.value == null) {
                                        _petImages.value = PetImage()
                                    }

                                    if (images[currentIndex].isCover) {
                                        _petImages.value = _petImages.value?.copy(
                                            imageCoverUrl = formatIbbUrl(response.url)
                                        )
                                    }

                                    _petImages.value = _petImages.value?.copy(
                                        imageUrls = _petImages.value?.imageUrls
                                            ?.plus(formatIbbUrl(response.url)) ?: listOf(formatIbbUrl(response.url)),
                                    )

                                    uploadImagesRecursively(images, currentIndex + 1)
                                }
                            }
                            is Resource.Error -> {
                                Log.e("UPDATE PET: STEP 1", "Error for image ${currentIndex+1}: ${result.message}")
                                _uploadState.value = UploadState.Error(result.message ?: "Unknown error")
                            }
                            is Resource.Loading -> {}
                        }
                    }
            } catch (e: Exception) {
                Log.e("UPDATE PET: STEP 1", "Failed to upload image ${currentIndex+1}: ${e.message}")
                _uploadState.value = UploadState.Error("Failed to upload image ${currentIndex+1}: ${e.message}")
            }
        }
    }

    private fun submitPetUpdate() {
        val petToUpdate = if (_imagesChanged.value) {
            _updatedPet.value?.copy(image = _petImages.value)
        } else {
            _updatedPet.value
        }

        if (petToUpdate == null) {
            _uploadState.value = UploadState.Error("No pet data to update")
            return
        }

        viewModelScope.launch {
            petUseCase.updatePet(petToUpdate)
                .catch {
                    _uploadState.value = UploadState.Error(it.message.toString())
                }
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            Log.d("UPDATE PET", "Pet updated successfully: ${result.data?.message}")
                            _uploadState.value = UploadState.Success
                        }
                        is Resource.Error -> {
                            Log.d("UPDATE PET", "Error updating pet: ${result.message}")
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

    private fun updateFieldsValidity() {
        val pet = _updatedPet.value
        _isFieldsValid.value = pet != null && pet.name?.isNotEmpty() == true &&
                pet.color?.isNotEmpty() == true && pet.weight != null && pet.age != null
    }
}