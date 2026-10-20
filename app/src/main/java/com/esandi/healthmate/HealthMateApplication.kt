package com.esandi.healthmate

import android.app.Application
import com.esandi.healthmate.di.AppContainer
import com.esandi.healthmate.di.DefaultAppContainer

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

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
