package com.esandi.healthmate.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.esandi.healthmate.HealthMateApplication
import com.esandi.healthmate.feature.dashboard.DashboardViewModel

/**
 * One factory that knows how to build every ViewModel that needs the database.
 *
 * Android creates ViewModels itself (so they survive screen rotation), which means it cannot
 * pass constructor arguments on its own. This factory fills that gap: it takes the shared
 * repository from the [AppContainer] and hands it to each ViewModel's constructor.
 * Screens use it with `viewModel(factory = AppViewModelProvider.Factory)`.
 */
object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            DashboardViewModel(healthMateApplication().container.healthRecordRepository)
        }
    }
}

/** Finds the running [HealthMateApplication] from the extras Android passes to the factory. */
fun CreationExtras.healthMateApplication(): HealthMateApplication =
    this[APPLICATION_KEY] as HealthMateApplication
