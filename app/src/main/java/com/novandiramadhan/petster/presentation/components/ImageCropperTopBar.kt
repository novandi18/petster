package com.novandiramadhan.petster.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mr0xf00.easycrop.CropState
import com.novandiramadhan.petster.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCropperTopBar(
    state: CropState
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = {
                    state.done(false)
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    state.reset()
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.RestartAlt,
                    contentDescription = stringResource(R.string.undo)
                )
            }

            IconButton(
                onClick = {
                    state.done(true)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Done,
                    contentDescription = stringResource(R.string.crop)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}