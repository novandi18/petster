package com.novandiramadhan.petster.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.novandiramadhan.petster.common.CustomNavType
import com.novandiramadhan.petster.domain.model.Volunteer
import com.novandiramadhan.petster.presentation.screen.ProfileScreen
import com.novandiramadhan.petster.presentation.screen.VolunteerMapsUpdateScreen
import kotlin.reflect.typeOf

fun NavGraphBuilder.accountGraph(navController: NavController) {
    composable<Destinations.Profile> {
        ProfileScreen(
            back = {
                navController.popBackStack()
            },
            navigateTo = { destination ->
                navController.navigate(destination)
            }
        )
    }

    composable<Destinations.VolunteerMapsUpdate>(
        typeMap = mapOf(
            typeOf<Volunteer>() to CustomNavType.VolunteerType
        )
    ) {  backStackEntry ->
        val data = requireNotNull(backStackEntry.toRoute<Destinations.VolunteerMapsUpdate>())

        VolunteerMapsUpdateScreen(
            volunteer = data.volunteer,
            back = {
                navController.popBackStack()
            },
            navigateTo = {  destination ->
                navController.navigate(destination) {
                    if (destination is Destinations.Profile) {
                        popUpTo(Destinations.Profile) {
                            inclusive = true
                        }
                    }
                }
            }
        )
    }
}