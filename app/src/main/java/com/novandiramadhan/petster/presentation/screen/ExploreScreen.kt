package com.novandiramadhan.petster.presentation.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.data.paging.PetPagingError
import com.novandiramadhan.petster.presentation.components.EmptyView
import com.novandiramadhan.petster.presentation.components.ErrorView
import com.novandiramadhan.petster.presentation.components.PetCard
import com.novandiramadhan.petster.presentation.navigation.Destinations
import com.novandiramadhan.petster.presentation.ui.theme.Black
import com.novandiramadhan.petster.presentation.ui.theme.LimeGreen
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import com.novandiramadhan.petster.presentation.viewmodel.ExploreViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.common.utils.isLocationEnabled
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.ShelterLocation
import com.novandiramadhan.petster.presentation.components.FilterDialog
import kotlin.toString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = hiltViewModel(),
    navigateTo: (Destinations) -> Unit = {},
) {
    val authState by viewModel.authState.collectAsState()
    val petFavStatus by viewModel.petFavStatus.collectAsState()
    val updatedPetId by viewModel.updatedPetId.collectAsState()
    val updatedPetFavorites by viewModel.updatedPetFavorites.collectAsState()
    val pets = viewModel.pets.collectAsLazyPagingItems()
    val refreshState = rememberPullToRefreshState()
    val isRefreshing = pets.loadState.refresh is LoadState.Loading
    var showFilterDialog by remember { mutableStateOf(false) }
    val currentFilters by viewModel.filterState.collectAsState()
    val isLocationFilterActive by viewModel.isLocationFilterActive.collectAsState()
    val context = LocalContext.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    var showLocationPermissionDialog by remember { mutableStateOf(false) }
    var showEnableGpsDialog by remember { mutableStateOf(false) }

    when (val status = petFavStatus) {
        is Resource.Loading -> {}
        is Resource.Success -> {
            Toast.makeText(
                LocalContext.current,
                status.data?.message,
                Toast.LENGTH_SHORT
            ).show()
            viewModel.resetAddFavStatus()
        }
        is Resource.Error -> {
            Toast.makeText(
                LocalContext.current,
                stringResource(status.messageResId ?: R.string.error_unknown),
                Toast.LENGTH_SHORT).show()

            viewModel.resetAddFavStatus()
        }
        null -> viewModel.resetAddFavStatus()
    }

    if (showFilterDialog) {
        FilterDialog(
            initialState = currentFilters,
            onDismissRequest = { showFilterDialog = false },
            onApplyFilters = { appliedFilters ->
                viewModel.updateFilters(appliedFilters)
                showFilterDialog = false
            }
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionsGranted = permissions.entries.all { it.value }
        if (allPermissionsGranted) {
            if (isLocationEnabled(locationManager)) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                try {
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        CancellationTokenSource().token
                    ).addOnSuccessListener { location ->
                        if (location != null) {
                            viewModel.toggleLocationFilter(
                                isActive = true,
                                location = ShelterLocation(location.latitude, location.longitude)
                            )
                        } else {
                            Toast.makeText(context, context.getString(R.string.failed_get_location), Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: SecurityException) {
                    Toast.makeText(context, context.getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
                }
            } else {
                showEnableGpsDialog = true
            }
        } else {
            showLocationPermissionDialog = true
        }
    }

    if (showLocationPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showLocationPermissionDialog = false },
            title = {
                Text(
                    text = context.getString(R.string.enable_location_title)
                )
            },
            text = {
                Text(
                    text = context.getString(R.string.enable_location_desc)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showEnableGpsDialog = false
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }) {
                    Text(context.getString(R.string.settings))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationPermissionDialog = false }) {
                    Text(context.getString(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.explore),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(
                        modifier = Modifier.size(48.dp),
                        onClick = { showFilterDialog = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = if (currentFilters.isNotEmpty) LimeGreen else
                                MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FilterAlt,
                            contentDescription = stringResource(R.string.filter),
                        )
                    }

                    IconButton(
                        modifier = Modifier.size(48.dp),
                        onClick = {
                            if (isLocationFilterActive) {
                                viewModel.toggleLocationFilter(false)
                            } else {
                                val fineLocationPermission = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.ACCESS_FINE_LOCATION
                                )
                                val coarseLocationPermission = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                                )

                                when {
                                    fineLocationPermission == PackageManager.PERMISSION_GRANTED ||
                                            coarseLocationPermission == PackageManager.PERMISSION_GRANTED -> {
                                        if (isLocationEnabled(locationManager)) {
                                            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                                            try {
                                                fusedLocationClient.getCurrentLocation(
                                                    Priority.PRIORITY_HIGH_ACCURACY,
                                                    CancellationTokenSource().token
                                                ).addOnSuccessListener { location ->
                                                    if (location != null) {
                                                        viewModel.toggleLocationFilter(
                                                            isActive = true,
                                                            location = ShelterLocation(location.latitude, location.longitude)
                                                        )
                                                    } else {
                                                        Toast.makeText(context, context.getString(R.string.failed_get_location), Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            } catch (e: SecurityException) {
                                                Toast.makeText(context, context.getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            showEnableGpsDialog = true
                                        }
                                    }
                                    else -> {
                                        locationPermissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    }
                                }
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = if (isLocationFilterActive) LimeGreen else
                                MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = stringResource(R.string.location),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            state = refreshState,
            onRefresh = {
                pets.refresh()
            },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            isRefreshing = isRefreshing,
            indicator = {
                Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = isRefreshing,
                    color = Black,
                    containerColor = LimeGreen,
                    state = refreshState
                )
            }
        ) {
            when {
                pets.loadState.refresh is LoadState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                pets.loadState.refresh is LoadState.Error -> {
                    val error = (pets.loadState.refresh as LoadState.Error).error
                    val errorMessage = when (error) {
                        is PetPagingError -> error.message
                        else -> stringResource(R.string.error_unknown)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorView(
                            title = errorMessage,
                            onRetry = {
                                pets.refresh()
                            }
                        )
                    }
                }

                pets.itemCount == 0 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyView(
                            title = stringResource(R.string.pet_list_available_empty),
                            desc = stringResource(R.string.pet_list_available_empty_message)
                        )
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        contentPadding = PaddingValues(16.dp),
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            count = pets.itemCount,
                            key = { index -> pets[index]?.id ?: index }
                        ) { index ->
                            val pet = pets[index] ?: return@items
                            PetCard(
                                pet = if (updatedPetId == pet.id?.toString()) {
                                    pet.copy(isFavorite = !pet.isFavorite)
                                } else if (updatedPetFavorites.containsKey(pet.id?.toString())) {
                                    pet.copy(isFavorite = updatedPetFavorites[pet.id?.toString()] ?: pet.isFavorite)
                                } else pet,
                                onClick = { destinations ->
                                    navigateTo(destinations)
                                },
                                isFavoriteShow = authState?.userType == UserType.SHELTER,
                                onFavoriteClick = { isFavorite ->
                                    if (authState?.userType == UserType.SHELTER) {
                                        viewModel.togglePetFavorite(
                                            petId = pet.id.toString(),
                                            isFavorite = isFavorite
                                        )
                                    }
                                },
                            )
                        }

                        if (pets.loadState.append is LoadState.Loading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreScreenPreview() {
    PetsterTheme {
        ExploreScreen()
    }
}