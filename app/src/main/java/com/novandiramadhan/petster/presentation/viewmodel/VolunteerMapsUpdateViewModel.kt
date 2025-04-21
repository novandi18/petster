package com.novandiramadhan.petster.presentation.viewmodel

import android.annotation.SuppressLint
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.novandiramadhan.petster.common.states.MapsUiState
import com.novandiramadhan.petster.common.utils.isLocationEnabled
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Volunteer
import com.novandiramadhan.petster.domain.model.VolunteerLocation
import com.novandiramadhan.petster.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VolunteerMapsUpdateViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(MapsUiState())
    val uiState: StateFlow<MapsUiState> = _uiState.asStateFlow()

    private val _updateResult = MutableStateFlow<Resource<Unit>?>(null)
    val updateResult: StateFlow<Resource<Unit>?> = _updateResult.asStateFlow()

    fun initWithVolunteerLocation(volunteer: Volunteer) {
        volunteer.location?.let { location ->
            if (location.latitude != null && location.longitude != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                _uiState.update {
                    it.copy(
                        selectedLocation = latLng,
                        selectedAddress = volunteer.address ?: "Selected location"
                    )
                }
                return
            }
        }
        _uiState.update {
            it.copy(
                selectedLocation = DEFAULT_LOCATION,
                selectedAddress = "Jakarta, Indonesia"
            )
        }
    }

    fun checkLocationPermission(isGranted: Boolean) {
        _uiState.update { it.copy(locationPermissionGranted = isGranted) }

        if (isGranted) {
            checkLocationEnabled()
        }
    }

    fun checkLocationEnabled(locationManager: LocationManager? = null) {
        locationManager?.let {
            val isEnabled = isLocationEnabled(it)
            _uiState.update { state -> state.copy(locationEnabled = isEnabled) }

            if (!isEnabled) {
                _uiState.update { it.copy(showLocationDialog = true) }
            }
        }
    }

    fun onLocationDialogDismiss() {
        _uiState.update { it.copy(showLocationDialog = false) }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        viewModelScope.launch {
            if (_uiState.value.locationPermissionGranted && _uiState.value.locationEnabled) {
                try {
                    _locationClient?.lastLocation?.addOnSuccessListener { location ->
                        if (location != null) {
                            val latLng = LatLng(location.latitude, location.longitude)
                            _uiState.update {
                                it.copy(
                                    selectedLocation = latLng,
                                    selectedAddress = "Current location"
                                )
                            }
                        }
                    }
                } catch (e: SecurityException) {
                    Log.e("VolunteerMapsUpdateViewModel", "getCurrentLocation: ${e.message}")
                }
            }
        }
    }

    fun onMapClick(latLng: LatLng) {
        _uiState.update {
            it.copy(
                selectedLocation = latLng,
                selectedAddress = "Lat: ${latLng.latitude}, Long: ${latLng.longitude}"
            )
        }
    }

    fun updateVolunteerLocation(volunteerUuid: String) {
        viewModelScope.launch {
            val location = _uiState.value.selectedLocation ?: return@launch
            val volunteerLocation = VolunteerLocation(
                latitude = location.latitude,
                longitude = location.longitude
            )
            authUseCase.updateVolunteerLocation(volunteerUuid, volunteerLocation)
                .catch { e ->
                    Log.e("VolunteerMapsUpdateViewModel", "updateVolunteerLocation: ${e.message}")
                    _updateResult.value = Resource.Error(e.message.toString())
                }
                .collect { result ->
                    _updateResult.value = result
                }
        }
    }

    fun clearUpdateResult() {
        _updateResult.value = null
    }

    companion object {
        private var _locationClient: FusedLocationProviderClient? = null
        val DEFAULT_LOCATION = LatLng(-6.200000, 106.816666)
    }
}