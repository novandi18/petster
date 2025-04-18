package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.utils.validatePhoneNumber
import com.novandiramadhan.petster.domain.model.Shelter
import com.novandiramadhan.petster.domain.model.ShelterForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateShelterProfile(
    shelter: Shelter,
    setShowDialog: (Boolean) -> Unit = {},
    onSubmit: (shelterForm: ShelterForm) -> Unit = { _ -> },
    isDisabled: Boolean = false
) {
    val sheetState = rememberModalBottomSheetState()
    var name by remember { mutableStateOf(shelter.name) }
    var phone by remember { mutableStateOf(shelter.phoneNumber) }
    var address by remember { mutableStateOf(shelter.address) }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            if (!isDisabled) setShowDialog(false)
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextFormField(
                    title = stringResource(R.string.name),
                    icon = Icons.Rounded.Person,
                    value = name ?: "",
                    onValueChange = { name = it },
                    enabled = !isDisabled
                )

                PhoneFormField(
                    value = phone ?: "",
                    onValueChange = { phone = it },
                    enabled = !isDisabled
                )

                TextFormField(
                    title = stringResource(R.string.address),
                    icon = Icons.Rounded.LocationOn,
                    value = address ?: "",
                    onValueChange = { address = it },
                    enabled = !isDisabled
                )
            }

            ElevatedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                onClick = {
                    onSubmit(
                        ShelterForm(
                            name = name ?: "",
                            email = "",
                            phoneNumber = phone ?: "",
                            password = "",
                            address = address ?: ""
                        )
                    )
                },
                enabled = validatePhoneNumber(phone ?: "") && name?.isNotEmpty() == true &&
                        address?.isNotEmpty() == true && !isDisabled,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (isDisabled) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.update_profile),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun UpdateShelterProfilePreview() {
    UpdateShelterProfile(
        shelter = Shelter(
            uuid = "1",
            name = "Shelter Name",
            address = "Shelter Address",
            phoneNumber = "1234567890"
        ),
        setShowDialog = {},
        onSubmit = {},
        isDisabled = false
    )
}