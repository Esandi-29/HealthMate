package com.esandi.healthmate.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

/**
 * Switch to a bottom-bar tab with standard tab behaviour: only one copy of each tab exists,
 * and each tab keeps its scroll position when the user comes back to it.
 */
fun NavHostController.navigateToTab(destination: HealthMateDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
