package com.novandiramadhan.petster.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PostAdd
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
import com.novandiramadhan.petster.presentation.components.PostCard
import com.novandiramadhan.petster.presentation.navigation.Destinations
import com.novandiramadhan.petster.presentation.ui.theme.Black
import com.novandiramadhan.petster.presentation.ui.theme.LimeGreen
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import com.novandiramadhan.petster.presentation.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = hiltViewModel(),
    navigateTo: (Destinations) -> Unit = {},
) {
    val posts = viewModel.communityPosts.collectAsLazyPagingItems()
    val refreshState = rememberPullToRefreshState()
    val isRefreshing = posts.loadState.refresh is LoadState.Loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.communities),
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
                onClick = {},
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
                        imageVector = Icons.Rounded.PostAdd,
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
                posts.refresh()
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
            when (posts.loadState.refresh) {
                is LoadState.Loading -> {
                    if (posts.itemCount == 0) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }

                is LoadState.Error -> {
                    val error = posts.loadState.refresh as LoadState.Error
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorView(
                            title = error.error.message ?: stringResource(R.string.community_error_unexpected),
                            onRetry = { posts.retry() }
                        )
                    }
                }

                is LoadState.NotLoading -> {
                    if (posts.itemCount == 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyView(
                                title = stringResource(R.string.community_post_available_empty),
                                desc = stringResource(R.string.community_post_available_empty_message)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(
                                count = posts.itemCount,
                                key = posts.itemKey { it.post?.id ?: "" }
                            ) { index ->
                                val post = posts[index]
                                post?.let {
                                    PostCard(
                                        post = it,
                                        onClick = {
                                            navigateTo(
                                                Destinations.CommunityPost(
                                                    postId = it.post?.id ?: "",
                                                )
                                            )
                                        },
                                        onLikeClick = { postId ->

                                        }
                                    )
                                }
                            }

                            when (posts.loadState.append) {
                                is LoadState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(modifier = Modifier.size(36.dp))
                                        }
                                    }
                                }
                                is LoadState.Error -> {
                                    val error = posts.loadState.append as LoadState.Error
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            ErrorView(
                                                title = error.error.message ?: stringResource(R.string.community_error_unexpected),
                                                onRetry = { posts.retry() },
                                            )
                                        }
                                    }
                                }
                                is LoadState.NotLoading -> {}
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCommunityScreen() {
    PetsterTheme {
        CommunityScreen()
    }
}