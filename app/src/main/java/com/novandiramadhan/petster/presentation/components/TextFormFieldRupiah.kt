package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.common.utils.formatRupiah
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@Composable
fun TextFormFieldRupiah(
    modifier: Modifier = Modifier,
    title: String,
    value: String = "",
    onValueChange: (String) -> Unit = {}
) {
    var formattedValue by remember(value) { mutableStateOf(formatRupiah(value)) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = formattedValue,
            onValueChange = { newValue ->
                val filteredValue = newValue.filter { it.isDigit() }
                formattedValue = formatRupiah(filteredValue)
                onValueChange(filteredValue)
            },
            label = {
                Text(text = title)
            },
            singleLine = true,
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
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                errorContainerColor = Color.Red.copy(alpha = .08f),
                errorLeadingIconColor = Color.Red,
                errorBorderColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            shape = CircleShape
        )

        Text(
            modifier = modifier,
            text = stringResource(R.string.adopt_fee_empty),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview
@Composable
private fun TextFormFieldRupiahPreview() {
    PetsterTheme {
        TextFormFieldRupiah(
            title = "Adopt Fee",
        )
    }
}