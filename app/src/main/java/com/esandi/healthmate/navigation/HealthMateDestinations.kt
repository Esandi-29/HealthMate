package com.esandi.healthmate.navigation

/**
 * Every screen in HealthMate and the route string Navigation Compose uses to reach it.
 *
 * Top-level destinations appear in the bottom bar. The others are "pushed" on top
 * temporarily (Add, Edit, Details), following Android's navigation principles.
 */
sealed class HealthMateDestination(val route: String) {

    // Bottom bar destinations
    data object Home : HealthMateDestination("home")
    data object History : HealthMateDestination("history")
    data object Progress : HealthMateDestination("progress")
    data object Profile : HealthMateDestination("profile")

    // Screens opened on top of the bottom bar
    data object Onboarding : HealthMateDestination("onboarding")
    data object AddRecord : HealthMateDestination("record/add")

    data object EditRecord : HealthMateDestination("record/edit/{recordId}") {
        fun createRoute(recordId: Long) = "record/edit/$recordId"
    }

    data object RecordDetails : HealthMateDestination("record/details/{recordId}") {
        fun createRoute(recordId: Long) = "record/details/$recordId"
    }

    // Style guide of colours, fonts and components (opened from Profile)
    data object DesignSystem : HealthMateDestination("design-system")

    companion object {
        const val RECORD_ID_ARG = "recordId"
        val bottomBarDestinations = listOf(Home, History, Progress, Profile)
    }
}
