package com.novandiramadhan.petster.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A custom Composable for creating a tabbed interface.
 *
 * @param tabs List of tab titles.
 * @param contentScreens List of Composable functions representing content screens for each tab.
 * @param modifier Modifier for the parent layout.
 * @param containerColor Background color for the tab row container.
 * @param contentColor Color for the text content of the tabs.
 * @param indicatorColor Color for the indicator line.
 */
@Composable
fun TabRowComponent(
    tabs: List<String>,
    selectedTabIndex: Int = 0,
    onTabSelected: (Int) -> Unit,
    contentScreens: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Gray,
    contentColor: Color = Color.White,
    indicatorColor: Color = Color.DarkGray,
    disabledContent: Boolean = false
) {
    Column(modifier = modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = containerColor,
            contentColor = contentColor,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = indicatorColor
                )
            }
        ) {
            tabs.forEachIndexed { index, tabTitle ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        if (!disabledContent) {
                            onTabSelected(index)
                        }
                    },
                    enabled = !disabledContent
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = tabTitle,
                        color = if (disabledContent) contentColor.copy(alpha = 0.5f) else contentColor
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            contentScreens.getOrNull(selectedTabIndex)?.invoke()

            if (disabledContent) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.5f))
                        .blur(radius = 3.dp)
                        .clickable(enabled = false) { }
                )
            }
        }
    }
}