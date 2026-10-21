package com.esandi.healthmate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.esandi.healthmate.data.model.HealthRecord
import com.esandi.healthmate.di.AppViewModelProvider
import com.esandi.healthmate.navigation.HealthMateBottomBar
import com.esandi.healthmate.navigation.HealthMateDestination
import com.esandi.healthmate.navigation.HealthMateNavHost
import com.esandi.healthmate.navigation.navigateToTab
import com.esandi.healthmate.ui.components.primaryMetric
import kotlinx.coroutines.launch

/**
 * The app shell: the bottom tab bar, the area where screens are shown, and the message bar
 * (snackbar) for confirmations such as "Steps record added" or "Record deleted - Undo".
 * The tab bar is visible on the four main tabs and hidden on pushed screens (Add, Edit, Details).
 */
@Composable
fun HealthMateApp(
    appViewModel: HealthMateAppViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // A new message replaces the one on screen instead of queueing behind it.
    fun showMessage(message: String) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
        }
    }

    // Deleting is undoable: the record is kept in memory while the message is visible.
    fun onRecordDeleted(record: HealthRecord) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = "${record.primaryMetric().label} record deleted",
                actionLabel = "Undo",
                duration = SnackbarDuration.Long,
            )
            if (result == SnackbarResult.ActionPerformed) appViewModel.undoDelete(record)
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val tabRoutes = HealthMateDestination.bottomBarDestinations.map { it.route }
    val showBottomBar = currentRoute == null || currentRoute in tabRoutes

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    shape = RoundedCornerShape(14.dp),
                    actionColor = MaterialTheme.colorScheme.inversePrimary,
                )
            }
        },
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
            onShowMessage = ::showMessage,
            onRecordDeleted = ::onRecordDeleted,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        )
    }
}
