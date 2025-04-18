package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.presentation.ui.theme.Black
import com.novandiramadhan.petster.presentation.ui.theme.LimeGreen
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TextFormMultiField(
    modifier: Modifier = Modifier,
    title: String,
    fields: List<String>,
    selectedFields: List<String> = emptyList(),
    onSelectionChanged: (List<String>) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val displayFields = if (expanded) fields else fields.take(6)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            displayFields.forEach { field ->
                FilterChip(
                    onClick = {
                        val newSelection = if (selectedFields.contains(field)) {
                            selectedFields.filter { it != field }
                        } else {
                            selectedFields + field
                        }
                        onSelectionChanged(newSelection)
                    },
                    label = { Text(text = field) },
                    selected = selectedFields.contains(field),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LimeGreen,
                        selectedLabelColor = Black,
                        containerColor = MaterialTheme.colorScheme.background,
                        labelColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            if (fields.size > 6) {
                FilterChip(
                    onClick = { expanded = !expanded },
                    label = {
                        Text(
                            text = if (expanded) "Show Less" else "Show More"
                        )
                    },
                    selected = false,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        labelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun TextFormMultiFieldPreview() {
    PetsterTheme {
        var selected by remember { mutableStateOf(listOf<String>()) }

        TextFormMultiField(
            title = "Pet Disabilities",
            fields = stringArrayResource(R.array.pet_disabilities).toList(),
            selectedFields = selected,
            onSelectionChanged = { selected = it }
        )
    }
}