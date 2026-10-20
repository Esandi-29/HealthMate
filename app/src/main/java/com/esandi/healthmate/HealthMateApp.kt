package com.esandi.healthmate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.esandi.healthmate.navigation.HealthMateBottomBar
import com.esandi.healthmate.navigation.HealthMateDestination
import com.esandi.healthmate.navigation.HealthMateNavHost
import com.esandi.healthmate.navigation.navigateToTab

/**
 * The app shell: the bottom tab bar plus the area where screens are shown.
 * The tab bar is visible on the four main tabs and hidden on pushed screens (Add, Edit, Details).
 */
@Composable
fun HealthMateApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val tabRoutes = HealthMateDestination.bottomBarDestinations.map { it.route }
    val showBottomBar = currentRoute == null || currentRoute in tabRoutes

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
            ) {
                HealthMateBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { destination -> navController.navigateToTab(destination) },
                    onAdd = { navController.navigate(HealthMateDestination.AddRecord.route) },
                )
            }
        },
    ) { innerPadding ->
        HealthMateNavHost(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        )
    }
}
