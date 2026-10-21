package com.esandi.healthmate.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.esandi.healthmate.HealthMateAppViewModel
import com.esandi.healthmate.HealthMateApplication
import com.esandi.healthmate.data.repository.HealthRecordRepository
import com.esandi.healthmate.feature.dashboard.DashboardViewModel
import com.esandi.healthmate.feature.history.HistoryViewModel
import com.esandi.healthmate.feature.record.RecordDetailsViewModel
import com.esandi.healthmate.feature.record.RecordFormViewModel

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
            DashboardViewModel(repository())
        }
        // createSavedStateHandle() gives the ViewModel the route arguments, e.g. {recordId}.
        initializer {
            RecordFormViewModel(createSavedStateHandle(), repository())
        }
        initializer {
            RecordDetailsViewModel(createSavedStateHandle(), repository())
        }
        initializer {
            HistoryViewModel(repository())
        }
        initializer {
            HealthMateAppViewModel(repository())
        }
    }
}

/** Finds the running [HealthMateApplication] from the extras Android passes to the factory. */
fun CreationExtras.healthMateApplication(): HealthMateApplication =
    this[APPLICATION_KEY] as HealthMateApplication

/** Shortcut for the shared repository held by the app's [AppContainer]. */
private fun CreationExtras.repository(): HealthRecordRepository =
    healthMateApplication().container.healthRecordRepository
