package com.esandi.healthmate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.esandi.healthmate.data.model.HealthRecord
import com.esandi.healthmate.di.AppViewModelProvider
import com.esandi.healthmate.navigation.HealthMateBottomBar
import com.esandi.healthmate.navigation.HealthMateDestination
import com.esandi.healthmate.navigation.HealthMateNavHost
import com.esandi.healthmate.navigation.navigateToTab
import com.esandi.healthmate.ui.components.BannerMessage
import com.esandi.healthmate.ui.components.StatusBannerHost
import com.esandi.healthmate.ui.components.bannerNoun
import com.esandi.healthmate.ui.components.primaryMetric
import com.esandi.healthmate.ui.components.valueText
import kotlinx.coroutines.launch

/**
 * The app shell: the bottom tab bar, the area where screens are shown, and the status banner
 * (an iOS-style pill at the top) for confirmations such as "250 ml of water logged" or
 * "250 ml of water deleted - Undo".
 * The tab bar is visible on the four main tabs and hidden on pushed screens (Add, Edit, Details).
 */
@Composable
fun HealthMateApp(
    appViewModel: HealthMateAppViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Saved: "3,000 steps logged" / "Water updated to 500 ml". A new banner replaces the old one.
    fun onRecordSaved(record: HealthRecord, wasEdit: Boolean) {
        val metric = record.primaryMetric()
        val value = record.valueText(metric)
        val message = if (wasEdit) {
            BannerMessage(metric, prefix = "${metric.label} updated to ", highlight = value)
        } else {
            BannerMessage(metric, highlight = value, suffix = "${metric.bannerNoun()} logged")
        }
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    // Deleting is undoable: the record is kept in memory while the banner is visible (~10 s).
    fun onRecordDeleted(record: HealthRecord) {
        val metric = record.primaryMetric()
        val message = BannerMessage(
            metric,
            highlight = record.valueText(metric),
            suffix = "${metric.bannerNoun()} deleted",
            actionLabel = "Undo",
        )
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(message)
            if (result == SnackbarResult.ActionPerformed) appViewModel.undoDelete(record)
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val tabRoutes = HealthMateDestination.bottomBarDestinations.map { it.route }
    val showBottomBar = currentRoute == null || currentRoute in tabRoutes

    Box(Modifier.fillMaxSize()) {
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
                onRecordSaved = ::onRecordSaved,
                onRecordDeleted = ::onRecordDeleted,
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
            )
        }

        // The banner floats over every screen, just below the status bar.
        StatusBannerHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding(),
        )
    }
}
