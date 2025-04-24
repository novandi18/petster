package com.novandiramadhan.petster.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.novandiramadhan.petster.presentation.screen.CommunityPostScreen

fun NavGraphBuilder.communityGraph(navController: NavController) {
    composable<Destinations.CommunityPost> { backStackEntry ->
        val community = requireNotNull(backStackEntry.toRoute<Destinations.CommunityPost>())

        CommunityPostScreen(
            postId = community.postId,
            back = {
                navController.popBackStack()
            },
        )
    }
}