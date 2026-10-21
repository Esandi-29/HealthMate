package com.esandi.healthmate

import android.app.Application
import com.esandi.healthmate.di.AppContainer
import com.esandi.healthmate.di.DefaultAppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Application entry point. Android creates this once, before any screen.
 *
 * It owns the [AppContainer], which builds the database and repository a single time
 * and hands them to the ViewModels. This is "manual dependency injection": a simple,
 * explainable alternative to libraries such as Hilt.
 */
class HealthMateApplication : Application() {

    lateinit var container: AppContainer
        private set

    /** Lives as long as the app; used for start-up work that must not block the UI. */
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)

        // Open the database in the background at launch. On the very first launch this
        // creates healthmate.db and inserts the sample records, so the first screen that
        // reads data does not have to wait for it.
        appScope.launch { container.healthRecordRepository.observeAll().first() }
    }
}
