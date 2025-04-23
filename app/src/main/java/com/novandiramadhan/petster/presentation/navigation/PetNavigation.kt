package com.novandiramadhan.petster.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.novandiramadhan.petster.presentation.screen.NewPetScreen
import com.novandiramadhan.petster.presentation.screen.PetScreen
import com.novandiramadhan.petster.presentation.screen.UpdatePetScreen

fun NavGraphBuilder.petGraph(navController: NavController) {

    composable<Destinations.PetDetail> { backStackEntry ->
        val pet = requireNotNull(backStackEntry.toRoute<Destinations.PetDetail>())

        PetScreen(
            petId = pet.petId,
            back = {
                navController.popBackStack()
            },
            navigateTo = { destination ->
                navController.navigate(destination) {
                    if (destination is Destinations.YourPets) {
                        popUpTo(Destinations.YourPets) {
                            inclusive = true
                        }
                    }
                }
            }
        )
    }

    composable<Destinations.NewPet> {
        NewPetScreen(
            back = {
                navController.popBackStack()
            }
        )
    }

    composable<Destinations.UpdatePet> { backStackEntry ->
        val pet = requireNotNull(backStackEntry.toRoute<Destinations.UpdatePet>())

        UpdatePetScreen(
            petId = pet.petId,
            back = {
                navController.popBackStack()
            }
        )
    }
}