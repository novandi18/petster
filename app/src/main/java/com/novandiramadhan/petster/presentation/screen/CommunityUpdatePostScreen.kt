package com.novandiramadhan.petster.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.PostAIPromptHelper
import com.novandiramadhan.petster.data.resource.Resource
import com.novandiramadhan.petster.presentation.components.PostAIButton
import com.novandiramadhan.petster.presentation.components.PostFormField
import com.novandiramadhan.petster.presentation.viewmodel.CommunityUpdatePostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityUpdatePostScreen(
    viewModel: CommunityUpdatePostViewModel = hiltViewModel(),
    postId: String,
    content: String,
    back: () -> Unit = {},
) {
    val context = LocalContext.current
    val postText by viewModel.postText.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val activeGenerationOption by viewModel.activeGenerationOption.collectAsState()
    val updatePost by viewModel.updatePost.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.updatePostText(content)
    }

    LaunchedEffect(updatePost) {
        if (updatePost is Resource.Success) {
            Toast.makeText(context, context.getString(R.string.community_post_success_update), Toast.LENGTH_SHORT).show()
            back()
        } else if (updatePost is Resource.Error) {
            Toast.makeText(context, updatePost?.message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.edit_community_post),
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                navigationIcon = {
                    IconButton(onClick = back) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    if (updatePost is Resource.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp).padding(end = 16.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    } else {
                        TextButton(
                            modifier = Modifier.padding(end = 12.dp),
                            onClick = {
                                if (postText.isNotEmpty()) {
                                    viewModel.updatePost(postId, postText)
                                }
                            },
                            enabled = postText.isNotEmpty(),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(.1f),
                                disabledContentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.update)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val aiOptions = stringArrayResource(R.array.create_community_post_ai)
                aiOptions.forEach { option ->
                    PostAIButton(
                        text = option,
                        isLoading = isGenerating && activeGenerationOption == option,
                        isDisabled = isGenerating || updatePost is Resource.Loading,
                    ) {
                        if (postText.isNotEmpty()) {
                            val prompt = PostAIPromptHelper.getPromptForOption(
                                context = context,
                                selectedOption = option,
                                postText = postText
                            )
                            viewModel.generateAIContent(prompt, option)
                        }
                    }
                }
            }
            PostFormField(
                value = postText,
                onValueChange = viewModel::updatePostText,
                isDisabled = isGenerating || updatePost is Resource.Loading
            )
        }
    }
}