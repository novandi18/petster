package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.dummy.PetDummy
import com.novandiramadhan.petster.domain.model.Pet
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@Composable
fun ProfilePets(
    modifier: Modifier = Modifier,
    pets: List<Pet> = emptyList(),
    onPetClick: (id: String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.your_pets),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6f)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            pets.forEach { pet ->
                OwnPetCard(
                    pet = pet,
                    onClick = {
                        if (pet.id != null) onPetClick(pet.id)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfilePetsPreview() {
    PetsterTheme {
        ProfilePets(
            pets = PetDummy(context = LocalContext.current).pets
        )
    }
}