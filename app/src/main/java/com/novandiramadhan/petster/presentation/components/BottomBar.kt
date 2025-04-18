package com.novandiramadhan.petster.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.novandiramadhan.petster.common.BottomBarItems
import com.novandiramadhan.petster.common.types.UserType
import com.novandiramadhan.petster.presentation.navigation.Destinations
import com.novandiramadhan.petster.presentation.ui.theme.PetsterTheme

@Composable
fun BottomBar(
    currentRoute: NavDestination? = null,
    navigateTo: (Destinations) -> Unit = {},
    userType: UserType = UserType.NONE
) {
    val items = BottomBarItems(userType).getItems()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (currentRoute?.hierarchy?.any {
                                it.hasRoute(item.route::class)
                            } == true) item.selectedIcon else item.icon,
                        contentDescription = stringResource(item.title)
                    )
                },
                selected = currentRoute?.hierarchy?.any {
                    it.hasRoute(item.route::class)
                } == true,
                onClick = {
                    navigateTo(item.route)
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onBackground,
                    unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                    unselectedTextColor = MaterialTheme.colorScheme.onBackground
                ),
            )
        }
    }
}

@Preview
@Composable
fun BottomBarPreview() {
    PetsterTheme {
        BottomBar()
    }
}