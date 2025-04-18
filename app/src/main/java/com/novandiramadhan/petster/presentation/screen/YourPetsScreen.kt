package com.novandiramadhan.petster.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.presentation.components.EmptyView
import com.novandiramadhan.petster.presentation.components.ErrorView
import com.novandiramadhan.petster.presentation.components.PetCard
import com.novandiramadhan.petster.presentation.navigation.Destinations
import com.novandiramadhan.petster.presentation.ui.theme.Black
import com.novandiramadhan.petster.presentation.ui.theme.LimeGreen
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import com.novandiramadhan.petster.presentation.viewmodel.YourPetsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourPetsScreen(
    viewModel: YourPetsViewModel = hiltViewModel(),
    navigateTo: (Destinations) -> Unit = {},
) {
    val volunteerPets = viewModel.volunteerPets.collectAsLazyPagingItems()
    val refreshState = rememberPullToRefreshState()
    val isRefreshing = volunteerPets.loadState.refresh is LoadState.Loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.your_pets),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navigateTo(Destinations.NewPet)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Pets,
                        contentDescription = stringResource(R.string.create_post)
                    )
                    Text(
                        text = stringResource(R.string.create_post),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            state = refreshState,
            onRefresh = {
                volunteerPets.refresh()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
            when (volunteerPets.loadState.refresh) {
                is LoadState.Loading -> {
                    if (volunteerPets.itemCount == 0) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }

                is LoadState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorView(
                            title = stringResource(R.string.your_pet_empty_error),
                            onRetry = {
                                volunteerPets.retry()
                            }
                        )
                    }
                }

                is LoadState.NotLoading -> {
                    if (volunteerPets.itemCount == 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyView(
                                title = stringResource(R.string.your_pet_empty),
                                desc = stringResource(R.string.your_pet_empty_desc)
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                count = volunteerPets.itemCount,
                                key = volunteerPets.itemKey {
                                    it.id ?: ""
                                }
                            ) { index ->
                                val pet = volunteerPets[index]
                                pet?.let {
                                    PetCard(
                                        pet = it,
                                        onClick = { destinations ->
                                            navigateTo(destinations)
                                        }
                                    )
                                }
                            }

                            item(span = { GridItemSpan(maxLineSpan) }) {
                                if (volunteerPets.loadState.append is LoadState.Loading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            item(span = { GridItemSpan(maxLineSpan) }) {
                                if (volunteerPets.loadState.append is LoadState.Error) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ErrorView(
                                            title = stringResource(R.string.your_pet_empty_error),
                                            onRetry = {
                                                volunteerPets.retry()
                                            }
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
}

@Preview(showBackground = true)
@Composable
private fun YourPetsScreenPreview() {
    PetsterTheme {
        YourPetsScreen()
    }
}