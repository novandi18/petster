package com.novandiramadhan.petster.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.ui.graphics.vector.ImageVector
import com.novandiramadhan.petster.R
import com.novandiramadhan.petster.common.icons.starsIcon

sealed class NavigationItem(
    val route: Destinations,
    val icon: ImageVector,
    val title: Int,
    val selectedIcon: ImageVector
) {
    data object Home: NavigationItem(
        Destinations.Home,
        Icons.Outlined.Home,
        R.string.home,
        Icons.Rounded.Home
    )

    data object Favorite: NavigationItem(
        Destinations.Favorite,
        Icons.Rounded.FavoriteBorder,
        R.string.favorite,
        Icons.Rounded.Favorite
    )

    data object YourPets: NavigationItem(
        Destinations.YourPets,
        Icons.Outlined.Pets,
        R.string.your_pets,
        Icons.Rounded.Pets
    )

    data object Explore: NavigationItem(
        Destinations.Explore,
        Icons.Outlined.Explore,
        R.string.explore,
        Icons.Rounded.Explore
    )

    data object Assistant: NavigationItem(
        Destinations.Assistant,
        starsIcon,
        R.string.assistant,
        starsIcon
    )

    data object Community: NavigationItem(
        Destinations.Community,
        Icons.Outlined.Groups,
        R.string.communities,
        Icons.Rounded.Groups
    )
}