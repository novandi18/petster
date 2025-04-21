package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.domain.model.Volunteer
import com.novandiramadhan.petster.presentation.navigation.Destinations
import com.novandiramadhan.petster.presentation.ui.theme.Black
import com.novandiramadhan.petster.presentation.ui.theme.LimeGreen

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

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = stringResource(R.string.your_location),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6f)
            )

            ElevatedButton(
                onClick = {
                    if (volunteer != null) {
                        navigateTo(
                            Destinations.VolunteerMapsUpdate(
                                volunteer = volunteer,
                            )
                        )
                    }
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = LimeGreen,
                    contentColor = Black
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = stringResource(
                            if (volunteer?.location?.latitude == null || volunteer.location.longitude == null)
                            R.string.add_location else R.string.change_location
                        ),
                    )
                    Text(
                        text = stringResource(
                            if (volunteer?.location?.latitude == null || volunteer.location.longitude == null)
                            R.string.add_location else R.string.change_location
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        ProfileSettings(
            modifier = Modifier
                .padding(top = 12.dp),
            onChangeEmail = onChangeEmail
        )
    }
}