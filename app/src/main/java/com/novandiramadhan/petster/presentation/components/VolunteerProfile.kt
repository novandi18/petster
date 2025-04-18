package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.domain.model.Volunteer
import com.novandiramadhan.petster.presentation.navigation.Destinations

@Composable
fun VolunteerProfile(
    volunteer: Volunteer? = null,
    navigateTo: (Destinations) -> Unit = {},
    onChangeEmail: () -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.name),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6f)
            )
            Text(
                text = volunteer?.name ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column {
            Text(
                text = stringResource(R.string.email),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6f)
            )
            Text(
                text = volunteer?.email ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column {
            Text(
                text = stringResource(R.string.phone_number),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6f)
            )
            Text(
                text = volunteer?.phoneNumber ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column {
            Text(
                text = stringResource(R.string.address),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6f)
            )
            Text(
                text = volunteer?.address ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        ProfileSettings(
            modifier = Modifier
                .padding(top = 12.dp),
            onChangeEmail = onChangeEmail
        )
    }
}