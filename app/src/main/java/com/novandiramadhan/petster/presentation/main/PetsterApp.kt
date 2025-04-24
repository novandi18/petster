package com.novandiramadhan.petster.presentation.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.presentation.components.BottomBar
import com.novandiramadhan.petster.presentation.navigation.Destinations
import com.novandiramadhan.petster.presentation.navigation.accountGraph
import com.novandiramadhan.petster.presentation.navigation.communityGraph
import com.novandiramadhan.petster.presentation.navigation.mainGraph
import com.novandiramadhan.petster.presentation.navigation.petGraph

@Composable
fun PetsterApp(
    navHostController: NavHostController = rememberNavController(),
    userLoggedInType: UserType = UserType.NONE
) {
    val navBackStackEntry = navHostController.currentBackStackEntryAsState().value
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = Destinations.allDestinations.any {
        currentDestination?.hierarchy?.any { destination ->
            destination.hasRoute(it::class) && it.showBottomBar
        } == true
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(visible = showBottomBar) {
                BottomBar(
                    currentRoute = currentDestination,
                    navigateTo = { destination ->
                        navHostController.navigate(destination)
                    },
                    userType = userLoggedInType
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navHostController,
            startDestination = Destinations.Welcome
        ) {
            mainGraph(navHostController)
            accountGraph(navHostController)
            petGraph(navHostController)
            communityGraph(navHostController)
        }
    }
}