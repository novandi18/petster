package com.novandiramadhan.petster.presentation.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.dummy.VolunteerDummy
import com.novandiramadhan.petster.domain.model.Volunteer
import com.novandiramadhan.petster.presentation.ui.theme.Black
import com.novandiramadhan.petster.presentation.ui.theme.LimeGreen
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme
import androidx.core.net.toUri

@Composable
fun VolunteerInformation(
    volunteer: Volunteer
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.volunteer_information),
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            VolunteerInformationText(
                title = stringResource(R.string.name),
                value = volunteer.name ?: ""
            )

            VolunteerInformationText(
                title = stringResource(R.string.email),
                value = volunteer.email ?: ""
            )

            VolunteerInformationText(
                title = stringResource(R.string.phone_number),
                value = volunteer.phoneNumber ?: ""
            )

            VolunteerInformationText(
                title = stringResource(R.string.address),
                value = volunteer.address ?: ""
            )
        }

        if (volunteer.location?.latitude != null && volunteer.location.longitude != null) {
            ElevatedButton(
                onClick = {
                    try {
                        val gmmIntentUri =
                            "geo:${volunteer.location.latitude},${volunteer.location.longitude}?q=${volunteer.location.latitude},${volunteer.location.longitude}".toUri()

                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")

                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            val browserIntent = Intent(Intent.ACTION_VIEW,
                                "https://maps.google.com/?q=${volunteer.location.latitude},${volunteer.location.longitude}".toUri()
                            )
                            context.startActivity(browserIntent)
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.open_gmaps_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = LimeGreen,
                    contentColor = Black
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = stringResource(R.string.location)
                    )
                    Text(
                        text = stringResource(R.string.open_location_in_gmaps),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun VolunteerInformationText(
    title: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6f)
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VolunteerInformationPreview() {
    PetsterTheme {
        VolunteerInformation(
            volunteer = VolunteerDummy.volunteers[0]
        )
    }
}