package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@Composable
fun FilterOptions(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .selectableGroup()
            .padding(vertical = 8.dp)
    ) {
        options.forEach { option ->
            FilterRadioButtonRow(
                text = option,
                selected = (selectedOption == option),
                onClick = { onOptionSelected(option) }
            )
        }
    }
}

@Preview
@Composable
private fun FilterOptionsPreview() {
    PetsterTheme {
        FilterOptions(
            options = listOf("Option 1", "Option 2", "Option 3"),
            selectedOption = "Option 1",
            onOptionSelected = {}
        )
    }
}