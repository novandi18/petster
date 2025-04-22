package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.utils.validateEmail
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@Composable
fun ChangeEmailDialog(
    currentEmail: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var email by remember { mutableStateOf(currentEmail) }
    var isValidEmail by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.change_email)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.change_email_logout_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                EmailFormField(
                    value = email,
                    onValueChange = {
                        email = it
                        isValidEmail = validateEmail(email)
                    }
                )
            }
        },
        confirmButton = {
            ElevatedButton(
                onClick = { onSubmit(email) },
                enabled = isValidEmail && email.isNotEmpty() && !isLoading && email != currentEmail,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.submit),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onBackground
    )
}

@Preview
@Composable
private fun ChangeEmailDialogPreview() {
    PetsterTheme {
        ChangeEmailDialog(
            currentEmail = "",
            isLoading = false,
            onDismiss = {},
            onSubmit = {}
        )
    }
}