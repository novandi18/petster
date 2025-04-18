package com.novandiramadhan.petster.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.presentation.components.AccountConnect
import com.novandiramadhan.petster.presentation.components.ShelterProfile
import com.novandiramadhan.petster.presentation.components.UpdateShelterProfile
import com.novandiramadhan.petster.presentation.components.UpdateVolunteerProfile
import com.novandiramadhan.petster.presentation.components.VolunteerProfile
import com.novandiramadhan.petster.presentation.navigation.Destinations
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import com.novandiramadhan.petster.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    back: () -> Unit = {},
    navigateTo: (Destinations) -> Unit = {}
) {
    val authState by viewModel.authState.collectAsState()
    val userState by viewModel.user.collectAsState()
    val updateProfileState by viewModel.updateProfileState.collectAsState()
    val isFormDisabled by viewModel.isFormDisabled.collectAsState()
    val volunteer by viewModel.volunteerProfile.collectAsState()
    val shelter by viewModel.shelterProfile.collectAsState()
    var showUpdateProfile by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.your_profile)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = back
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if (userState is Resource.Success && authState?.userType != UserType.NONE) {
                        IconButton(onClick = { showUpdateProfile = true }) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = stringResource(R.string.update_profile)
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.logout()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Logout,
                                contentDescription = stringResource(R.string.logout)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        when (val resource = userState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            is Resource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    val notLoggedIn = resource.message?.contains("not logged in", ignoreCase = true) == true ||
                            resource.message?.contains("UUID is missing", ignoreCase = true) == true

                    if (notLoggedIn) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            AccountConnect(navigateTo = navigateTo)
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = resource.message ?: stringResource(R.string.internet_error),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.refreshUserProfile() }) {
                                Text(stringResource(R.string.retry))
                            }
                        }
                    }
                }
            }

            is Resource.Success -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (volunteer != null) {
                        VolunteerProfile(
                            volunteer = volunteer,
                            navigateTo = { destinations ->
                                navigateTo(destinations)
                            },
                            onChangeEmail = {}
                        )
                    } else if (shelter != null) {
                        ShelterProfile(
                            shelter = shelter,
                            navigateTo = { destinations ->
                                navigateTo(destinations)
                            },
                            onChangeEmail = {}
                        )
                    } else {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Profile loaded but user data is unavailable.")
                        }
                    }
                }
            }
        }

        if (showUpdateProfile && authState?.userType != UserType.NONE) {
            when (val state = updateProfileState) {
                is Resource.Loading -> {}
                is Resource.Error -> {
                    Toast.makeText(
                        context,
                        stringResource(state.messageResId ?: R.string.update_profile_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Success -> {
                    showUpdateProfile = false
                    viewModel.refreshUserProfile()
                    viewModel.resetUpdateProfileState()
                    Toast.makeText(
                        context,
                        stringResource(R.string.update_profile_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                null -> {}
            }

            if (authState?.userType == UserType.SHELTER) {
                UpdateShelterProfile(
                    onSubmit = { form ->
                        shelter!!.uuid?.let {
                            viewModel.updateShelterProfile(
                                form = form,
                                uuid = shelter!!.uuid!!
                            )
                        }
                    },
                    setShowDialog = {
                        showUpdateProfile = it
                    },
                    shelter = shelter!!,
                    isDisabled = isFormDisabled,
                )
            } else {
                UpdateVolunteerProfile(
                    onSubmit = { form ->
                        volunteer!!.uuid?.let {
                            viewModel.updateVolunteerProfile(
                                form = form,
                                uuid = volunteer!!.uuid!!
                            )
                        }
                    },
                    setShowDialog = {
                        showUpdateProfile = it
                    },
                    volunteer = volunteer!!,
                    isDisabled = isFormDisabled,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    PetsterTheme {
        ProfileScreen()
    }
}