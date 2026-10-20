package com.esandi.healthmate.di

import android.content.Context

/**
 * Holds the objects that live for the whole lifetime of the app and are shared by
 * every screen, such as the Room database and the HealthRecord repository.
 *
 * They are added in Phase 3 (task 3.1). Keeping them in one place means each
 * ViewModel asks the container for what it needs instead of building it itself.
 */
interface AppContainer {
    // val healthRecordRepository: HealthRecordRepository   <- added in task 3.1
}

class DefaultAppContainer(
    private val context: Context,
) : AppContainer
