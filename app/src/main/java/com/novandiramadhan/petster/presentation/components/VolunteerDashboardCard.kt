package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.domain.model.VolunteerDashboardResult
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@Composable
fun VolunteerDashboardCard(
    volunteerDashboardResult: VolunteerDashboardResult
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Text(
            text = stringResource(R.string.dashboard),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DashboardItemCard(
                title = stringResource(R.string.total_pets),
                count = volunteerDashboardResult.totalPets
            )
            DashboardItemCard(
                title = stringResource(R.string.adopted),
                count = volunteerDashboardResult.adoptedPets
            )
            DashboardItemCard(
                title = stringResource(R.string.total_views),
                count = volunteerDashboardResult.totalViews
            )
        }
    }
}

@Composable
private fun DashboardItemCard(title: String, count: Int) {
    Column(
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Preview
@Composable
fun PreviewVolunteerDashboardCard() {
    PetsterTheme {
        VolunteerDashboardCard(
            volunteerDashboardResult = VolunteerDashboardResult(
                totalPets = 10,
                adoptedPets = 5,
                totalViews = 100
            )
        )
    }
}