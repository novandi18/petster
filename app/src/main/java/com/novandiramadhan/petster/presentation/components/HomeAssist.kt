package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.presentation.navigation.Destinations
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@Composable
fun HomeAssist(
    navigateTo: (Destinations) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Text(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
            text = stringResource(R.string.how_can_help),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge
        )

        Column(
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            HomeAssistCard(
                text = stringResource(R.string.volunteer),
                onClick = {
                    navigateTo(Destinations.VolunteerConnect)
                }
            )

            HomeAssistCard(
                text = stringResource(R.string.adopt_pet),
                onClick = {
                    navigateTo(Destinations.ShelterConnect)
                }
            )
        }
    }
}

@Preview
@Composable
private fun HomeAssistPreview() {
    PetsterTheme {
        HomeAssist()
    }
}