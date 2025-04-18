package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.novandiramadhan.petster.common.utils.validateEmail
import com.novandiramadhan.petster.common.utils.validatePassword
import com.novandiramadhan.petster.domain.model.SignInForm
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@Composable
fun SignInForm(
    onSubmit: (signInForm: SignInForm) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EmailFormField(
                value = email,
                onValueChange = { email = it }
            )

            PasswordFormField(
                value = password,
                onValueChange = { password = it }
            )
        }

        ElevatedButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            onClick = {
                if (validateEmail(email) && validatePassword(password)) {
                    onSubmit(
                        SignInForm(
                            email = email,
                            password = password
                        )
                    )
                }
            },
            enabled = email.isNotBlank() && password.isNotBlank(),
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
private fun SignInPreview() {
    PetsterTheme {
        SignInForm { _ -> }
    }
}