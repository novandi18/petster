package com.novandiramadhan.petster.common.states

import com.google.android.gms.maps.model.LatLng

data class MapsUiState(
    val locationPermissionGranted: Boolean = false,
    val locationEnabled: Boolean = false,
    val showLocationDialog: Boolean = false,
    val selectedLocation: LatLng? = null,
    val selectedAddress: String = ""
)
