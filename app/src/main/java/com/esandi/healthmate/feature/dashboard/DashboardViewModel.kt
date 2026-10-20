package com.esandi.healthmate.feature.dashboard

import androidx.lifecycle.ViewModel
import com.esandi.healthmate.data.model.DailyGoals
import com.esandi.healthmate.data.model.DailyTotals
import com.esandi.healthmate.data.model.HealthRecord
import com.esandi.healthmate.data.sample.SampleData
import com.esandi.healthmate.ui.components.primaryMetric
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/** Everything the Dashboard needs to draw itself. The screen only reads this; it never changes it. */
data class DashboardUiState(
    val userName: String = "Esandi",
    val today: LocalDate = LocalDate.now(),
    val week: List<LocalDate> = emptyList(),
    val daysWithRecords: Set<LocalDate> = emptySet(),
    val totals: DailyTotals = DailyTotals(),
    val goals: DailyGoals = DailyGoals(),
    val recentRecords: List<HealthRecord> = emptyList(),
) {
    val stepsProgress: Float get() = progress(totals.steps, goals.steps)

    /** "Today's goal": the average progress of all four metrics, each capped at 100%. */
    val overallProgress: Float
        get() = listOf(
            progress(totals.steps, goals.steps),
            progress(totals.calories, goals.calories),
            progress(totals.waterMl, goals.waterMl),
            progress(totals.sleepMinutes, goals.sleepMinutes),
        ).average().toFloat()

    private fun progress(value: Int, goal: Int): Float =
        if (goal <= 0) 0f else (value.toFloat() / goal).coerceIn(0f, 1f)
}

/**
 * ViewModel for the Dashboard (MVVM): prepares the screen state and exposes it as a StateFlow.
 * The screen observes [uiState] and redraws automatically whenever it changes.
 *
 * For now the data comes from [SampleData]; in Phase 3 (task 3.3) it will come from the
 * HealthRecord repository (Room database), and the screen code will not need to change.
 */
class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(buildState(LocalDate.now()))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private fun buildState(today: LocalDate): DashboardUiState {
        val records = SampleData.records(today)
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return DashboardUiState(
            today = today,
            week = (0L..6L).map { monday.plusDays(it) },
            daysWithRecords = records.map { it.date }.toSet(),
            totals = DailyTotals.of(records.filter { it.date == today }),
            recentRecords = latestPerMetric(records),
        )
    }

    companion object {
        /**
         * The most recent entry for each metric (steps, calories, water, sleep), newest first,
         * so the Dashboard preview list shows every metric once. History still lists every entry.
         */
        fun latestPerMetric(records: List<HealthRecord>): List<HealthRecord> =
            records
                .sortedWith(compareByDescending<HealthRecord> { it.date }.thenByDescending { it.time })
                .distinctBy { it.primaryMetric() }
    }
}
