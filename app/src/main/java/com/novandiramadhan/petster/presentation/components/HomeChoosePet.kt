package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.domain.model.PetHome
import com.novandiramadhan.petster.presentation.navigation.Destinations
import com.novandiramadhan.petster.presentation.ui.theme.LimeGreen
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@Composable
fun HomeChoosePet(
    navigateTo: (Destinations) -> Unit = {},
    selectedCategory: String = "All",
    setCategory: (String) -> Unit = {},
    pets: PetHome
) {
    val isEmpty = pets.dog.isEmpty() && pets.cat.isEmpty() && pets.other.isEmpty()

    val currentPets = when (selectedCategory.lowercase()) {
        "dog" -> pets.dog
        "cat" -> pets.cat
        "other" -> pets.other
        else -> pets.dog + pets.cat + pets.other
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.choose_pet),
                style = MaterialTheme.typography.titleLarge,
            )
            if (!isEmpty) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        modifier = Modifier.size(48.dp),
                        onClick = { },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FilterAlt,
                            contentDescription = stringResource(R.string.filter),
                        )
                    }

                    IconButton(
                        modifier = Modifier.size(48.dp),
                        onClick = { },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = stringResource(R.string.location),
                        )
                    }
                }
            }
        }

        if (isEmpty) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 28.dp, end = 28.dp, top = 16.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.pet_list_available_empty),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.pet_list_available_empty_message),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val categories = stringArrayResource(R.array.home_pet_categories).toList()

                categories.forEach { category ->
                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .defaultMinSize(minWidth = 24.dp),
                        onClick = {
                            setCategory(category)
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = if (category == selectedCategory) LimeGreen else
                                MaterialTheme.colorScheme.background,
                            contentColor = if (category == selectedCategory)
                                MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp
                        ),
                        contentPadding = PaddingValues(),
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (category == selectedCategory) FontWeight.Bold else FontWeight.Normal
                            ),
                        )
                    }
                }
            }

            if (currentPets.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 28.dp, end = 28.dp, top = 16.dp, bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 40.dp),
                        text = "No ${selectedCategory.lowercase()} pets available",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(
                        start = 24.dp, end = 24.dp, top = 8.dp
                    )
                ) {
                    val petsToShow = if (currentPets.size > 10) currentPets.take(4) else currentPets
                    items(petsToShow) { pet ->
                        PetCard(
                            pet = pet,
                            onClick = navigateTo
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomeChoosePetPreview() {
    PetsterTheme {
        HomeChoosePet(
            pets = PetHome(
                dog = listOf(),
                cat = listOf(),
                other = listOf()
            )
        )
    }
}