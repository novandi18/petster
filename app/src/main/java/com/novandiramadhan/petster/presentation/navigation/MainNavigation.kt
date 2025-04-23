package com.novandiramadhan.petster.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.novandiramadhan.petster.presentation.screen.AssistantScreen
import com.novandiramadhan.petster.presentation.screen.CommunityScreen
import com.novandiramadhan.petster.presentation.screen.ExploreScreen
import com.novandiramadhan.petster.presentation.screen.FavoriteScreen
import com.novandiramadhan.petster.presentation.screen.HomeScreen
import com.novandiramadhan.petster.presentation.screen.ShelterConnectScreen
import com.novandiramadhan.petster.presentation.screen.VolunteerConnectScreen
import com.novandiramadhan.petster.presentation.screen.WelcomeScreen
import com.novandiramadhan.petster.presentation.screen.YourPetsScreen

fun NavGraphBuilder.mainGraph(navController: NavController) {
    composable<Destinations.Welcome> {
        WelcomeScreen(
            navigateToHome = {
                navController.navigate(Destinations.Home) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                }
            }
        )
    }

    composable<Destinations.Home> {
        HomeScreen(
            navigateTo = { destination ->
                navController.navigate(destination)
            }
        )
    }

    composable<Destinations.Favorite> {
        FavoriteScreen(
            navigateTo = { destination ->
                navController.navigate(destination)
            }
        )
    }

    composable<Destinations.VolunteerConnect> {
        VolunteerConnectScreen(
            back = {
                navController.popBackStack()
            }
        )
    }

    composable<Destinations.ShelterConnect> {
        ShelterConnectScreen(
            back = {
                navController.popBackStack()
            }
        )
    }

    composable<Destinations.YourPets> {
        YourPetsScreen(
            navigateTo = { destination ->
                navController.navigate(destination)
            }
        )
    }

    composable<Destinations.Explore> {
        ExploreScreen(
            navigateTo = { destination ->
                navController.navigate(destination)
            }
        )
    }

    composable<Destinations.Assistant> {
        AssistantScreen()
    }

    composable<Destinations.Community> {
        CommunityScreen()
    }
}