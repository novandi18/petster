package com.novandiramadhan.petster.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.utils.capitalize
import com.novandiramadhan.petster.common.utils.formatPostDate
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.domain.model.PostComment
import com.novandiramadhan.petster.domain.model.PostResult
import com.novandiramadhan.petster.domain.model.UserResult
import com.novandiramadhan.petster.presentation.components.CommunityPostMenu
import com.novandiramadhan.petster.presentation.components.PostCommentCard
import com.novandiramadhan.petster.presentation.components.PostCommentField
import com.novandiramadhan.petster.presentation.navigation.Destinations
import com.novandiramadhan.petster.presentation.ui.theme.Black
import com.novandiramadhan.petster.presentation.ui.theme.LimeGreen
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import com.novandiramadhan.petster.presentation.ui.theme.Red
import com.novandiramadhan.petster.presentation.viewmodel.CommunityPostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityPostScreen(
    viewModel: CommunityPostViewModel = hiltViewModel(),
    postId: String,
    back: () -> Unit = {},
    navigateTo: (Destinations) -> Unit = {}
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    val postState by viewModel.post.collectAsState()
    val replyToCommentId by viewModel.replyToCommentId.collectAsState()
    val replyToAuthorName by viewModel.replyToAuthorName.collectAsState()
    val commentState by viewModel.comment.collectAsState()
    val deletePost by viewModel.deletePost.collectAsState()
    val showMore = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val refreshState = rememberPullToRefreshState()
    val refreshing = postState is Resource.Loading

    val isAuthor = remember(postState, authState) {
        if (postState is Resource.Success && authState != null) {
            val post = postState?.data?.post
            post?.authorId == authState?.uuid
        } else false
    }

    LaunchedEffect(postId, authState) {
        authState?.let { auth ->
            auth.uuid?.let { uuid ->
                viewModel.getPost(postId, uuid)
            }
        }
    }

    LaunchedEffect(commentState) {
        when (commentState) {
            is Resource.Success -> {
                authState?.uuid?.let { uuid ->
                    viewModel.getPost(postId, uuid)
                    viewModel.resetCommentState()
                    viewModel.clearReplyTo()
                }
            }
            is Resource.Error -> {
                Toast.makeText(
                    context,
                    commentState?.message ?: context.getString(R.string.community_error_comment_failed),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetCommentState()
            }
            else -> {}
        }
    }

    LaunchedEffect(deletePost) {
        when (deletePost) {
            is Resource.Success -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.delete_pet_success),
                    Toast.LENGTH_SHORT
                ).show()
                back()
                navigateTo(Destinations.Community)
            }
            is Resource.Error -> {
                Toast.makeText(
                    context,
                    deletePost?.message ?: context.getString(R.string.community_error_delete_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text(stringResource(R.string.delete_post)) },
            text = { Text(stringResource(R.string.delete_pet_confirmation)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog.value = false
                        viewModel.deletePost(postId)
                        Toast.makeText(
                            context,
                            context.getString(R.string.delete_pet_success),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Red
                    )
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog.value = false }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }

    CommunityPostMenu(
        onEditClick = {
            navigateTo(Destinations.CommunityUpdatePost(
                postId = postId,
                content = postState?.data?.post?.content ?: "",
            ))
        },
        onDeleteClick = {
            showDeleteDialog.value = true
        },
        isVisible = showMore.value,
        onDismiss = { showMore.value = false }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.posts),
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                navigationIcon = {
                    IconButton(onClick = back) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    if (isAuthor) {
                        IconButton(
                            onClick = {
                                showMore.value = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MoreHoriz,
                                contentDescription = stringResource(R.string.more),
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            PostCommentField(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        clip = false
                    ),
                onSubmit = { comment ->
                    authState?.let { auth ->
                        auth.uuid?.let { uuid ->
                            val newComment = PostComment(
                                authorId = uuid,
                                authorType = auth.userType.name.lowercase(),
                                comment = comment,
                                replyToCommentId = replyToCommentId
                            )
                            viewModel.addComment(postId, newComment)
                        }
                    }
                },
                replyOnCommentId = replyToCommentId,
                replyOnCommentAuthorName = replyToAuthorName,
                onCancelReply = {
                    viewModel.clearReplyTo()
                },
                isLoading = commentState is Resource.Loading
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            state = refreshState,
            onRefresh = {
                authState?.uuid?.let { uuid ->
                    viewModel.getPost(postId, uuid)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            isRefreshing = refreshing,
            indicator = {
                Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = refreshing,
                    color = Black,
                    containerColor = LimeGreen,
                    state = refreshState
                )
            }
        ) {
            when (postState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = postState?.message ?: stringResource(R.string.error_unknown),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = {
                            authState?.uuid?.let { uuid ->
                                viewModel.getPost(postId, uuid)
                            }
                        }) {
                            Text(text = stringResource(R.string.retry))
                        }
                    }
                }
                is Resource.Success -> {
                    val postData = postState?.data

                    if (postData != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            CommunityPostContent(
                                post = postData,
                                onLikeClick = { postIdToLike ->
                                    viewModel.toggleLike(postIdToLike, !postData.isLiked)
                                }
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val comments = postData.post?.comments ?: emptyList()
                                if (comments.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = stringResource(R.string.no_comments),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                } else {
                                    comments.forEach { comment ->
                                        PostCommentCard(
                                            data = comment,
                                            replyAuthorName = comment.replyToCommentId?.let { replyId ->
                                                comments.find { it.id == replyId }?.let { replyComment ->
                                                    when (val author = replyComment.author) {
                                                        is UserResult.ShelterResult -> author.shelter.name
                                                        is UserResult.VolunteerResult -> author.volunteer.name
                                                        else -> null
                                                    }
                                                }
                                            },
                                            onReply = {
                                                val authorName = when (val author = comment.author) {
                                                    is UserResult.ShelterResult -> author.shelter.name
                                                    is UserResult.VolunteerResult -> author.volunteer.name
                                                    else -> "Unknown"
                                                }
                                                viewModel.setReplyTo(comment.id, authorName)
                                            },
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.community_post_not_found),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                null -> {
                    Box(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
private fun CommunityPostContent(
    post: PostResult,
    onLikeClick: (String) -> Unit,
) {
    val authorName = when (val author = post.post?.author) {
        is UserResult.ShelterResult -> author.shelter.name ?: "Unknown Shelter"
        is UserResult.VolunteerResult -> author.volunteer.name ?: "Unknown Volunteer"
        else -> "Unknown User"
    }

    val postId = post.post?.id ?: ""
    val postContent = post.post?.content ?: ""
    val postDate = post.post?.createdAt

    Column(
        modifier = Modifier.padding(bottom = 20.dp, top = 8.dp, start = 24.dp, end = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.onBackground
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = authorName.first().toString(),
                        color = MaterialTheme.colorScheme.background
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = authorName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = post.post?.authorType?.capitalize() ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                }

                postDate?.let {
                    Text(
                        text = formatPostDate(it),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = postContent,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onLikeClick(postId) }
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(R.string.like),
                        tint = if (post.isLiked) Red else MaterialTheme.colorScheme.onSurface.copy(.7f)
                    )
                }

                Text(
                    text = "${post.likeCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Comment,
                    contentDescription = stringResource(R.string.comments),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "${post.commentCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCommunityPostScreen() {
    PetsterTheme {
        CommunityPostScreen(
            postId = "1"
        )
    }
}