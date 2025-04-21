package com.novandiramadhan.petster.presentation.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.Volunteer
import com.novandiramadhan.petster.presentation.navigation.Destinations
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import com.novandiramadhan.petster.presentation.viewmodel.VolunteerMapsUpdateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolunteerMapsUpdateScreen(
    viewModel: VolunteerMapsUpdateViewModel = hiltViewModel(),
    volunteer: Volunteer,
    back: () -> Unit = {},
    navigateTo: (Destinations) -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val locationManager = remember {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    val updateResult by viewModel.updateResult.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.checkLocationPermission(isGranted)
        if (isGranted) {
            viewModel.checkLocationEnabled(locationManager)
            viewModel.getCurrentLocation()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initWithVolunteerLocation(volunteer)
    }

    LaunchedEffect(Unit) {
        viewModel.initWithVolunteerLocation(volunteer)
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    LaunchedEffect(uiState.locationPermissionGranted, uiState.locationEnabled) {
        if (uiState.locationPermissionGranted && uiState.locationEnabled) {
            if (volunteer.location == null ||
                volunteer.location.latitude == null ||
                volunteer.location.longitude == null) {
                viewModel.getCurrentLocation()
            }
        }
    }

    val locationSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.checkLocationEnabled(locationManager)
        viewModel.getCurrentLocation()
    }

    val cameraPositionState = rememberCameraPositionState {
        position = uiState.selectedLocation?.let {
            CameraPosition.fromLatLngZoom(it, 14f)
        } ?: CameraPosition.fromLatLngZoom(VolunteerMapsUpdateViewModel.DEFAULT_LOCATION, 12f)
    }

    LaunchedEffect(uiState.selectedLocation) {
        uiState.selectedLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 14f)
        }
    }

    val mapUiSettings = remember(uiState.locationPermissionGranted, uiState.locationEnabled) {
        MapUiSettings(
            zoomControlsEnabled = true,
            compassEnabled = true,
            myLocationButtonEnabled = uiState.locationPermissionGranted && uiState.locationEnabled
        )
    }

    val mapProperties = remember(uiState.locationPermissionGranted, uiState.locationEnabled) {
        MapProperties(
            isMyLocationEnabled = uiState.locationPermissionGranted && uiState.locationEnabled,
            mapType = MapType.NORMAL
        )
    }

    if (uiState.showLocationDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onLocationDialogDismiss() },
            title = { Text(text = stringResource(R.string.enable_location_title)) },
            text = { Text(text = stringResource(R.string.enable_location_desc)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onLocationDialogDismiss()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    locationSettingsLauncher.launch(intent)
                }) {
                    Text("Enable")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onLocationDialogDismiss() }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (updateResult is Resource.Success) {
        Toast.makeText(
            context,
            stringResource(updateResult?.messageResId ?: R.string.update_location_success),
            Toast.LENGTH_SHORT
        ).show()
        LaunchedEffect(Unit) {
            back()
            navigateTo(Destinations.Profile)
        }
    } else if (updateResult is Resource.Error) {
        Toast.makeText(context, updateResult?.message, Toast.LENGTH_SHORT).show()
        viewModel.clearUpdateResult()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.your_location))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                navigationIcon = {
                    IconButton(onClick = back) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    uiState.selectedLocation?.let {
                        TextButton(
                            modifier = Modifier.padding(end = 8.dp),
                            enabled = updateResult !is Resource.Loading,
                            onClick = {
                                volunteer.uuid?.let { uuid ->
                                    viewModel.updateVolunteerLocation(uuid)
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.choose),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings,
                onMapClick = { viewModel.onMapClick(it) }
            ) {
                uiState.selectedLocation?.let { location ->
                    Marker(
                        state = MarkerState(position = location),
                        title = stringResource(R.string.selected_location),
                        snippet = uiState.selectedAddress
                    )
                }
            }

            if (updateResult is Resource.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun VolunteerMapsUpdateScreenPreview() {
    PetsterTheme {
        VolunteerMapsUpdateScreen(
            volunteer = Volunteer()
        )
    }
}