package com.esandi.healthmate.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.esandi.healthmate.data.model.HealthRecord
import com.esandi.healthmate.feature.dashboard.DashboardScreen
import com.esandi.healthmate.feature.history.HistoryScreen
import com.esandi.healthmate.feature.profile.ProfileScreen
import com.esandi.healthmate.feature.progress.ProgressScreen
import com.esandi.healthmate.feature.record.RecordDetailsScreen
import com.esandi.healthmate.feature.record.RecordFormScreen
import com.esandi.healthmate.navigation.HealthMateDestination.Companion.RECORD_ID_ARG
import com.esandi.healthmate.ui.theme.ThemeShowcaseScreen

private const val PUSH_DURATION = 320
private const val FADE_DURATION = 200

/**
 * The navigation graph: every screen and how screens open each other.
 * Tab screens cross-fade; Add/Edit/Details slide in from the right and back out, like iOS.
 */
@Composable
fun HealthMateNavHost(
    navController: NavHostController,
    onShowMessage: (String) -> Unit,
    onRecordDeleted: (HealthRecord) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HealthMateDestination.Home.route,
        modifier = modifier,
        enterTransition = { fadeIn(tween(FADE_DURATION)) },
        exitTransition = { fadeOut(tween(FADE_DURATION)) },
        popEnterTransition = { fadeIn(tween(FADE_DURATION)) },
        popExitTransition = { fadeOut(tween(FADE_DURATION)) },
    ) {
        // ---- Bottom-bar tabs ----
        composable(HealthMateDestination.Home.route) {
            DashboardScreen(
                onAddRecord = { navController.navigate(HealthMateDestination.AddRecord.route) },
                onOpenRecord = { id -> navController.navigate(HealthMateDestination.RecordDetails.createRoute(id)) },
                onSeeAllRecords = { navController.navigateToTab(HealthMateDestination.History) },
            )
        }
        composable(HealthMateDestination.History.route) {
            HistoryScreen(onOpenRecord = { id -> navController.navigate(HealthMateDestination.RecordDetails.createRoute(id)) })
        }
        composable(HealthMateDestination.Progress.route) {
            ProgressScreen()
        }
        composable(HealthMateDestination.Profile.route) {
            ProfileScreen(onOpenDesignSystem = { navController.navigate(HealthMateDestination.DesignSystem.route) })
        }

        // ---- Screens pushed on top ----
        pushedComposable(HealthMateDestination.AddRecord.route) {
            RecordFormScreen(
                onBack = { navController.popBackStack() },
                onSaved = { message -> navController.popBackStack(); onShowMessage(message) },
            )
        }
        pushedComposable(HealthMateDestination.EditRecord.route, recordIdArgument) {
            // The form's ViewModel reads {recordId} from the route itself (SavedStateHandle).
            RecordFormScreen(
                onBack = { navController.popBackStack() },
                onSaved = { message -> navController.popBackStack(); onShowMessage(message) },
            )
        }
        pushedComposable(HealthMateDestination.RecordDetails.route, recordIdArgument) {
            // The details ViewModel reads {recordId} from the route itself (SavedStateHandle).
            RecordDetailsScreen(
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(HealthMateDestination.EditRecord.createRoute(id)) },
                onDeleted = { record -> navController.popBackStack(); onRecordDeleted(record) },
            )
        }
        pushedComposable(HealthMateDestination.DesignSystem.route) {
            ThemeShowcaseScreen(onBack = { navController.popBackStack() })
        }
    }
}

/** The {recordId} part of a route is a Long (the database row id). */
private val recordIdArgument = listOf(navArgument(RECORD_ID_ARG) { type = NavType.LongType })

/** A destination that slides in from the right (iOS "push") and slides back out on Back. */
private fun NavGraphBuilder.pushedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable(
        route = route,
        arguments = arguments,
        enterTransition = { slideIntoContainer(SlideDirection.Start, tween(PUSH_DURATION)) },
        exitTransition = { slideOutOfContainer(SlideDirection.Start, tween(PUSH_DURATION), targetOffset = { it / 4 }) },
        popEnterTransition = { slideIntoContainer(SlideDirection.End, tween(PUSH_DURATION), initialOffset = { it / 4 }) },
        popExitTransition = { slideOutOfContainer(SlideDirection.End, tween(PUSH_DURATION)) },
        content = content,
    )
}
