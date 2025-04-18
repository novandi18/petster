package com.novandiramadhan.petster.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.presentation.components.AdoptionSteps
import com.novandiramadhan.petster.presentation.components.BenefitsOfAdoptionSection
import com.novandiramadhan.petster.presentation.components.HomeAssist
import com.novandiramadhan.petster.presentation.components.HomeChoosePet
import com.novandiramadhan.petster.presentation.components.HomeNewPet
import com.novandiramadhan.petster.presentation.components.HomeTopBar
import com.novandiramadhan.petster.presentation.components.VolunteerDashboardCard
import com.novandiramadhan.petster.presentation.navigation.Destinations
import com.novandiramadhan.petster.presentation.ui.theme.Black
import com.novandiramadhan.petster.presentation.ui.theme.LimeGreen
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import com.novandiramadhan.petster.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateTo: (Destinations) -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val refreshState = rememberPullToRefreshState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            HomeTopBar(
                onProfileClick = {
                    navigateTo(Destinations.Profile)
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { innerPadding: PaddingValues ->
        PullToRefreshBox(
            state = refreshState,
            onRefresh = {
                if (authState?.userType != UserType.NONE && authState != null) {
                    viewModel.refresh()
                } else {
                    scope.launch {
                        refreshState.animateToHidden()
                    }
                }
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
            HomeScreenContent(
                viewModel = viewModel,
                navigateTo = navigateTo
            )
        }
    }
}

@Composable
private fun HomeScreenContent(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    navigateTo: (Destinations) -> Unit
) {
    val authState by viewModel.authState.collectAsState()

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        authState?.let { state ->
            when (state.userType) {
                UserType.NONE -> {
                    HomeAssist(
                        navigateTo = { destinations ->
                            navigateTo(destinations)
                        }
                    )
                    BenefitsOfAdoptionSection()
                }
                UserType.SHELTER -> {
                    HomeShelterContent(
                        viewModel = viewModel,
                        navigateTo = { destinations ->
                            navigateTo(destinations)
                        }
                    )
                }
                UserType.VOLUNTEER -> {
                    HomeVolunteerContent(
                        viewModel = viewModel,
                        navigateTo = { destinations ->
                            navigateTo(destinations)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeShelterContent(
    viewModel: HomeViewModel,
    navigateTo: (Destinations) -> Unit
) {
    val pets by viewModel.pets.collectAsState()
    val selectedCategory = remember { mutableStateOf("All") }

    AdoptionSteps()

    when (val allPets = pets) {
        is Resource.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(32.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        is Resource.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp),
                    text = stringResource(allPets.messageResId ?: R.string.error_unknown),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                ElevatedButton(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                    onClick = {
                        viewModel.refresh()
                    },
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    ),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.retry),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        is Resource.Success -> {
            if (allPets.data != null) {
                HomeChoosePet(
                    navigateTo = navigateTo,
                    pets = allPets.data,
                    selectedCategory = selectedCategory.value,
                    setCategory = { category ->
                        selectedCategory.value = category
                    }
                )
            }
        }
        null -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(32.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun HomeVolunteerContent(
    viewModel: HomeViewModel,
    navigateTo: (Destinations) -> Unit
) {
    val dashboard by viewModel.volunteerDashboard.collectAsState()

    HomeNewPet(
        navigateTo = {
            navigateTo(Destinations.NewPet)
        }
    )

    when (val dashboardResult = dashboard) {
        is Resource.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(32.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        is Resource.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp),
                    text = stringResource(dashboardResult.messageResId ?: R.string.error_unknown),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
        is Resource.Success -> {
            if (dashboardResult.data != null) {
                VolunteerDashboardCard(
                    volunteerDashboardResult = dashboardResult.data
                )
            }
        }
        null -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(32.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    PetsterTheme {
        HomeScreen(
            navigateTo = {}
        )
    }
}