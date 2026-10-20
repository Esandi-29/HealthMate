package com.esandi.healthmate.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/** One tab in the bottom bar: where it goes, its label, and its selected/unselected icons. */
private data class TabItem(
    val destination: HealthMateDestination,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val leftTabs = listOf(
    TabItem(HealthMateDestination.Home, "Home", Icons.Rounded.Home, Icons.Outlined.Home),
)
private val rightTabs = listOf(
    TabItem(HealthMateDestination.History, "History", Icons.Rounded.CalendarMonth, Icons.Outlined.CalendarMonth),
    TabItem(HealthMateDestination.Progress, "Progress", Icons.Rounded.BarChart, Icons.Outlined.BarChart),
    TabItem(HealthMateDestination.Profile, "Profile", Icons.Rounded.Person, Icons.Outlined.Person),
)

/**
 * iOS-style tab bar in the wireframe's order: Home · Add · History · Progress · Profile.
 * Selected tabs use a filled icon in the primary colour (no Material "pill" indicator);
 * the centre Add button opens the Add Record screen instead of switching tab.
 */
@Composable
fun HealthMateBottomBar(
    currentRoute: String?,
    onNavigate: (HealthMateDestination) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = Color.Transparent,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Column(modifier) {
        // Hairline separator above the bar, as on iOS
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.96f),
            tonalElevation = 0.dp,
        ) {
            leftTabs.forEach { tab -> TabBarItem(tab, currentRoute, onNavigate, colors) }
            NavigationBarItem(
                selected = false,
                onClick = onAdd,
                icon = {
                    Box(
                        Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                label = { Text("Add") },
                colors = colors,
            )
            rightTabs.forEach { tab -> TabBarItem(tab, currentRoute, onNavigate, colors) }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.TabBarItem(
    tab: TabItem,
    currentRoute: String?,
    onNavigate: (HealthMateDestination) -> Unit,
    colors: NavigationBarItemColors,
) {
    val selected = currentRoute == tab.destination.route
    NavigationBarItem(
        selected = selected,
        onClick = { onNavigate(tab.destination) },
        icon = { Icon(if (selected) tab.selectedIcon else tab.unselectedIcon, contentDescription = null) },
        label = { Text(tab.label) },
        colors = colors,
    )
}
