package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.utils.validatePassword
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import com.novandiramadhan.petster.presentation.ui.theme.Red

@Composable
fun PasswordFormField(
    value: String = "",
    onValueChange: (String) -> Unit = {},
) {
    var hasInteracted by remember { mutableStateOf(false) }
    var passwordVisibility by remember { mutableStateOf(false) }

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
                text = stringResource(R.string.password) + " *"
            )
        },
        isError = hasInteracted && !validatePassword(value),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused.not() && value.isNotEmpty()) {
                    hasInteracted = true
                }
            },
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = stringResource(R.string.password)
            )
        },
        trailingIcon = {
            val image = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(
                    imageVector = image,
                    contentDescription = null
                )
            }
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
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onBackground,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onBackground,
            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
            errorLeadingIconColor = Red,
            errorTrailingIconColor = Red,
            unfocusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .04f),
            focusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .08f),
            errorContainerColor = Red.copy(alpha = .08f),
            errorBorderColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        shape = CircleShape
    )
    if (hasInteracted && !validatePassword(value)) {
        Text(
            text = stringResource(R.string.password_not_long_enough),
            color = Red,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview
@Composable
private fun PasswordFormFieldPreview() {
    PetsterTheme {
        PasswordFormField()
    }
}