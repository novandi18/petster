package com.novandiramadhan.petster.common

import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.presentation.navigation.NavigationItem

class BottomBarItems(
    private val userType: UserType = UserType.NONE
) {
    fun getItems(): List<NavigationItem> = when (userType) {
        UserType.VOLUNTEER -> {
            listOf(
                NavigationItem.Home,
                NavigationItem.YourPets,
                NavigationItem.Community,
            )
        }
        UserType.SHELTER -> {
            listOf(
                NavigationItem.Home,
                NavigationItem.Explore,
                NavigationItem.Assistant,
                NavigationItem.Favorite,
                NavigationItem.Community,
            )
        }
        else -> {
            listOf(
                NavigationItem.Home,
                NavigationItem.Explore,
                NavigationItem.Community,
            )
        }
    }
}