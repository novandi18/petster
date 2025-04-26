package com.novandiramadhan.petster.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.novandiramadhan.petster.presentation.screen.CommunityNewPostScreen
import com.novandiramadhan.petster.presentation.screen.CommunityPostScreen
import com.novandiramadhan.petster.presentation.screen.CommunityUpdatePostScreen

fun NavGraphBuilder.communityGraph(navController: NavController) {
    composable<Destinations.CommunityPost> { backStackEntry ->
        val community = requireNotNull(backStackEntry.toRoute<Destinations.CommunityPost>())

        CommunityPostScreen(
            postId = community.postId,
            back = {
                navController.popBackStack()
            },
            navigateTo = { destination ->
                navController.navigate(destination) {
                    if (destination is Destinations.Community) {
                        popUpTo(Destinations.Community) {
                            inclusive = true
                        }
                    }
                }
            }
        )
    }

    composable<Destinations.CommunityNewPost> { backStackEntry ->
        CommunityNewPostScreen(
            back = {
                navController.popBackStack()
            },
            navigateTo = { destination ->
                navController.navigate(destination) {
                    if (destination is Destinations.Community) {
                        popUpTo(Destinations.Community) {
                            inclusive = true
                        }
                    }
                }
            }
        )
    }

    composable<Destinations.CommunityUpdatePost> { backStackEntry ->
        val community = requireNotNull(backStackEntry.toRoute<Destinations.CommunityUpdatePost>())

        CommunityUpdatePostScreen(
            postId = community.postId,
            content = community.content,
            back = {
                navController.popBackStack()
            },
        )
    }
}