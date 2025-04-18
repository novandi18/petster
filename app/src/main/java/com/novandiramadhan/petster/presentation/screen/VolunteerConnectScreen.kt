package com.novandiramadhan.petster.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.AuthState
import com.novandiramadhan.petster.domain.model.SignInForm
import com.novandiramadhan.petster.domain.model.VolunteerForm
import com.novandiramadhan.petster.presentation.components.SignInForm
import com.novandiramadhan.petster.presentation.components.TabRowComponent
import com.novandiramadhan.petster.presentation.components.VolunteerSignUpForm
import com.novandiramadhan.petster.presentation.ui.theme.LimeGreen
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import com.novandiramadhan.petster.presentation.viewmodel.VolunteerConnectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolunteerConnectScreen(
    viewModel: VolunteerConnectViewModel = hiltViewModel(),
    back: () -> Unit = {}
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current
    val selectedTabIndex = remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.connect_as_volunteer),
                        style = MaterialTheme.typography.titleMedium
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            when (val state = authState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                is Resource.Success -> {
                    Toast.makeText(
                        context,
                        stringResource(
                            if (state.data?.isLoginType == true) {
                                R.string.login_volunteer_success
                            } else {
                                R.string.register_volunteer_success
                            }
                        ),
                        Toast.LENGTH_SHORT
                    ).show()

                    if (state.data?.isLoginType == true) {
                        viewModel.saveAuthData(
                            authState = AuthState(
                                uuid = state.data.volunteer?.uuid,
                                email = state.data.volunteer?.email,
                                userType = UserType.VOLUNTEER
                            )
                        )

                        back()
                    } else {
                        selectedTabIndex.intValue = 0
                    }

                    viewModel.resetAuthState()
                }
                is Resource.Error -> {
                    if (state.messageResId != null) {
                        Toast.makeText(
                            context,
                            stringResource(state.messageResId),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.resetAuthState()
                }

                null -> viewModel.resetAuthState()
            }

            TabRowComponent(
                tabs = listOf(
                    stringResource(R.string.sign_in),
                    stringResource(R.string.sign_up)
                ),
                contentScreens = listOf(
                    {
                        SignInForm(
                            onSubmit = { signInForm: SignInForm ->
                                if (!signInForm.email.isNullOrBlank() && !signInForm.password.isNullOrBlank()) {
                                    viewModel.signIn(
                                        email = signInForm.email, password = signInForm.password
                                    )
                                }
                            }
                        )
                    },
                    {
                        VolunteerSignUpForm(
                            onSubmit = { volunteerForm: VolunteerForm ->
                                viewModel.signUp(
                                    volunteerForm = volunteerForm
                                )
                            }
                        )
                    }
                ),
                containerColor = MaterialTheme.colorScheme.background,
                indicatorColor = LimeGreen,
                contentColor = MaterialTheme.colorScheme.onBackground,
                selectedTabIndex = selectedTabIndex.intValue,
                onTabSelected = { index ->
                    selectedTabIndex.intValue = index
                },
                disabledContent = authState is Resource.Loading,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VolunteerConnectScreenPreview() {
    PetsterTheme {
        VolunteerConnectScreen()
    }
}