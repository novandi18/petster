package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.dummy.PetDummy
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.presentation.ui.theme.Black
import com.novandiramadhan.petster.presentation.ui.theme.LimeGreen
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@Composable
fun PetInformation(
    pet: Pet = PetDummy(context = LocalContext.current).pets[0]
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
            text = pet.name ?: "",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .background(
                        color = LimeGreen,
                        shape = CircleShape
                    )
                    .padding(vertical = 4.dp, horizontal = 12.dp),
                text = pet.category ?: stringArrayResource(R.array.pet_categories).last(),
                style = MaterialTheme.typography.titleSmall,
                color = Black
            )
            Text(
                modifier = Modifier
                    .background(
                        color = LimeGreen,
                        shape = CircleShape
                    )
                    .padding(vertical = 4.dp, horizontal = 12.dp),
                text = pet.gender ?: "",
                style = MaterialTheme.typography.titleSmall,
                color = Black
            )
            Text(
                modifier = Modifier
                    .background(
                        color = LimeGreen,
                        shape = CircleShape
                    )
                    .padding(vertical = 4.dp, horizontal = 12.dp),
                text = "${pet.age} ${pet.ageUnit}",
                style = MaterialTheme.typography.titleSmall,
                color = Black
            )
        }

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
            // Coloration
            PetInformationText(
                title = stringResource(R.string.coloration),
                value = pet.color ?: ""
            )

            // Size
            PetInformationText(
                title = stringResource(R.string.size),
                value = pet.size ?: ""
            )

            // Breed
            if (!pet.breed.isNullOrEmpty()) {
                PetInformationText(
                    title = stringResource(R.string.breed),
                    value = pet.breed
                )
            }

            // Weight
            PetInformationText(
                title = stringResource(R.string.weight),
                value = stringResource(
                    R.string.weight_value,
                    pet.weight ?: "",
                    pet.weightUnit ?: stringArrayResource(R.array.pet_weight_units).first()
                )
            )

            // Vaccinated
            PetInformationText(
                title = stringResource(R.string.vaccinated),
                value = stringResource(
                    if (pet.isVaccinated) R.string.vaccinated_yes else R.string.vaccinated_no
                )
            )
        }

        if (pet.disabilities != null && pet.disabilities.isNotEmpty()) {
            PetHashtag(
                title = stringResource(R.string.pet_disabilities),
                hashtags = pet.disabilities
            )
        }

        if (pet.disabilities != null && pet.disabilities.isNotEmpty()) {
            PetHashtag(
                title = stringResource(R.string.pet_behaviour),
                hashtags = pet.behaviours ?: emptyList()
            )
        }
    }
}

@Composable
private fun PetInformationText(
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

@Preview
@Composable
private fun PetInformationPreview() {
    PetsterTheme {
        PetInformation()
    }
}