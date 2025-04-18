package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.novandiramadhan.petster.domain.model.VolunteerForm
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@Composable
fun VolunteerSignUpForm(
    onSubmit: (volunteerForm: VolunteerForm) -> Unit = { _ -> }
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

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
                value = name,
                onValueChange = { name = it }
            )

            EmailFormField(
                value = email,
                onValueChange = { email = it }
            )

            PasswordFormField(
                value = password,
                onValueChange = { password = it }
            )

            PhoneFormField(
                value = phone,
                onValueChange = { phone = it }
            )

            TextFormField(
                title = stringResource(R.string.address),
                icon = Icons.Rounded.LocationOn,
                value = address,
                onValueChange = { address = it }
            )
        }

        ElevatedButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            onClick = {
                onSubmit(
                    VolunteerForm(
                        name = name,
                        email = email,
                        phoneNumber = phone,
                        address = address,
                        password = password
                    )
                )
            },
            enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank() &&
                    phone.isNotBlank() && address.isNotBlank(),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.submit),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview
@Composable
private fun VolunteerSignUpFormPreview() {
    PetsterTheme {
        VolunteerSignUpForm()
    }
}