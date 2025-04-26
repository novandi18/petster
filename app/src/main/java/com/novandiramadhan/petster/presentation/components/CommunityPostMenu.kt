package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityPostMenu(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    var showSheet by remember { mutableStateOf(isVisible) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showSheet != isVisible) {
        showSheet = isVisible
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
                onDismiss()
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(id = R.string.edit_post),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = stringResource(id = R.string.edit_post),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .clickable {
                    onEditClick()
                    onDismiss()
                }
            )

            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(id = R.string.delete_post),
                        color = Color.Red
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(id = R.string.delete_post),
                        tint = Color.Red
                    )
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .clickable {
                    onDeleteClick()
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
private fun PreviewCommunityPostMenu() {
    PetsterTheme {
        var showMenu by remember { mutableStateOf(true) }

        CommunityPostMenu(
            onEditClick = {},
            onDeleteClick = {},
            isVisible = showMenu,
            onDismiss = { showMenu = false }
        )
    }
}