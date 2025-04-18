package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.dummy.VolunteerDummy
import com.novandiramadhan.petster.domain.model.Volunteer
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@Composable
fun VolunteerInformation(
    volunteer: Volunteer
) {
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