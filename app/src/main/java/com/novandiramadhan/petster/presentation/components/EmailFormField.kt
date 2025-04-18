package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.utils.validateEmail
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import com.novandiramadhan.petster.presentation.ui.theme.Red

@Composable
fun EmailFormField(
    value: String = "",
    onValueChange: (String) -> Unit = {}
) {
    var hasInteracted by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
            if (it.isNotEmpty()) {
                hasInteracted = true
            }
        },
        label = {
            Text(
                text = stringResource(R.string.email) + " *"
            )
        },
        isError = hasInteracted && !validateEmail(value),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused.not() && value.isNotEmpty()) {
                    hasInteracted = true
                }
            },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = stringResource(R.string.email)
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.onBackground,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .6f),
            focusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
            errorLeadingIconColor = Red,
            unfocusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .04f),
            focusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .08f),
            errorContainerColor = Red.copy(alpha = .08f),
            errorBorderColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        shape = CircleShape
    )
    if (hasInteracted && !validateEmail(value)) {
        Text(
            text = stringResource(R.string.email_invalid),
            color = Red,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview
@Composable
private fun EmailFormFieldPreview() {
    PetsterTheme {
        EmailFormField()
    }
}