package com.novandiramadhan.petster.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
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

    data object Notification: NavigationItem(
        Destinations.Notification,
        Icons.Outlined.Notifications,
        R.string.notification,
        Icons.Rounded.Notifications
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

    data object Article: NavigationItem(
        Destinations.Article,
        Icons.AutoMirrored.Outlined.Article,
        R.string.article,
        Icons.AutoMirrored.Rounded.Article
    )

    data object Assistant: NavigationItem(
        Destinations.Assistant,
        starsIcon,
        R.string.assistant,
        starsIcon
    )
}