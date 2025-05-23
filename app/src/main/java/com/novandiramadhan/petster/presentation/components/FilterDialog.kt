package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.PetFilterOptions
import com.novandiramadhan.petster.common.states.PetFilterState
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@Composable
fun FilterDialog(
    initialState: PetFilterState = PetFilterState(),
    onDismissRequest: () -> Unit,
    onApplyFilters: (PetFilterState) -> Unit
) {
    val context = LocalContext.current
    var selectedAdoptionFeeRange by remember { mutableStateOf(initialState.selectedAdoptionFeeRange) }
    var selectedCategory by remember { mutableStateOf(initialState.selectedCategory) }
    var selectedGender by remember { mutableStateOf(initialState.selectedGender) }
    var selectedVaccinated by remember { mutableStateOf(initialState.selectedVaccinated) }
    var selectedSize by remember { mutableStateOf(initialState.selectedSize) }

    val resetFilters = {
        selectedAdoptionFeeRange = null
        selectedCategory = null
        selectedGender = null
        selectedVaccinated = null
        selectedSize = null
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.filter_options),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f, fill = false)
                ) {
                    ExpandableFilterSection(
                        title = stringResource(R.string.adopt_fee),
                        selectedValue = selectedAdoptionFeeRange  // Pass selected value
                    ) {
                        FilterOptions(
                            options = PetFilterOptions(context).adoptionFeeRanges,
                            selectedOption = selectedAdoptionFeeRange,
                            onOptionSelected = { option ->
                                selectedAdoptionFeeRange = if (selectedAdoptionFeeRange == option) null else option
                            }
                        )
                    }

                    ExpandableFilterSection(
                        title = stringResource(R.string.pet_category),
                        selectedValue = selectedCategory
                    ) {
                        FilterOptions(
                            options = PetFilterOptions(context).categories,
                            selectedOption = selectedCategory,
                            onOptionSelected = { option ->
                                selectedCategory = if (selectedCategory == option) null else option
                            }
                        )
                    }

                    ExpandableFilterSection(
                        title = stringResource(R.string.gender),
                        selectedValue = selectedGender
                    ) {
                        FilterOptions(
                            options = PetFilterOptions(context).genders,
                            selectedOption = selectedGender,
                            onOptionSelected = { option ->
                                selectedGender = if (selectedGender == option) null else option
                            }
                        )
                    }

                    ExpandableFilterSection(
                        title = stringResource(R.string.vaccinated),
                        selectedValue = selectedVaccinated
                    ) {
                        FilterOptions(
                            options = PetFilterOptions(context).vaccinated,
                            selectedOption = selectedVaccinated,
                            onOptionSelected = { option ->
                                selectedVaccinated = if (selectedVaccinated == option) null else option
                            }
                        )
                    }

                    ExpandableFilterSection(
                        title = stringResource(R.string.pet_size),
                        selectedValue = selectedSize
                    ) {
                        FilterOptions(
                            options = PetFilterOptions(context).size,
                            selectedOption = selectedSize,
                            onOptionSelected = { option ->
                                selectedSize = if (selectedSize == option) null else option
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = resetFilters) {
                        Text(stringResource(R.string.clear_all))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onDismissRequest) {
                        Text(stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val appliedFilters = PetFilterState(
                                selectedAdoptionFeeRange = selectedAdoptionFeeRange,
                                selectedCategory = selectedCategory,
                                selectedGender = selectedGender,
                                selectedVaccinated = selectedVaccinated,
                                selectedSize = selectedSize
                            )
                            onApplyFilters(appliedFilters)
                        }
                    ) {
                        Text(stringResource(R.string.apply))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun FilterDialogPreview() {
    PetsterTheme {
        FilterDialog(
            onDismissRequest = {},
            onApplyFilters = {}
        )
    }
}