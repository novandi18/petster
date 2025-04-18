package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import com.novandiramadhan.petster.presentation.ui.theme.Red

@Composable
fun TextFormField(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
    isRequired: Boolean = true,
    enabled: Boolean = true
) {
    var hasInteracted by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused.not() && value.isNotEmpty()) {
                        hasInteracted = true
                    }
                },
            value = value,
            onValueChange = {
                onValueChange(it)
                if (it.isNotEmpty()) {
                    hasInteracted = true
                }
            },
            label = {
                Text(text = title + (if (isRequired) " *" else ""))
            },
            enabled = enabled,
            isError = if (isRequired && hasInteracted) value.isEmpty() else false,
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = title
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
                unfocusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .04f),
                focusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .08f),
                errorContainerColor = Red.copy(alpha = .08f),
                errorLeadingIconColor = Red,
                errorBorderColor = Color.Transparent,
                disabledTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                disabledLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                disabledContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .02f),
                disabledBorderColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            shape = CircleShape
        )
        if (isRequired && hasInteracted && value.isEmpty()) {
            Text(
                modifier = modifier,
                text = stringResource(R.string.field_required, title),
                color = Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
private fun TextFormFieldPreview() {
    PetsterTheme {
        TextFormField(
            title = "Email",
            icon = Icons.Default.Email
        )
    }
}