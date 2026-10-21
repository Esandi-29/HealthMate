package com.esandi.healthmate.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esandi.healthmate.data.model.DailyGoals
import com.esandi.healthmate.data.model.DailyTotals
import com.esandi.healthmate.data.model.HealthRecord
import com.esandi.healthmate.data.repository.HealthRecordRepository
import com.esandi.healthmate.ui.components.primaryMetric
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
 * The data comes live from the Room database through [repository]:
 *  - today's totals are added up by SQLite itself (SUM ... WHERE date = today);
 *  - this week's records give the dots on the week strip;
 *  - all records give the "Latest records" list.
 * Room re-runs these queries whenever health_records changes, so after a record is added,
 * edited or deleted the Dashboard updates by itself - no refresh code is needed.
 */
class DashboardViewModel(
    private val repository: HealthRecordRepository,
) : ViewModel() {

    private val today: LocalDate = LocalDate.now()
    private val week: List<LocalDate> =
        today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).let { monday -> (0L..6L).map { monday.plusDays(it) } }

    val uiState: StateFlow<DashboardUiState> =
        combine(
            repository.observeTotals(today),
            repository.observeBetween(week.first(), week.last()),
            repository.observeAll(),
        ) { totals, weekRecords, allRecords ->
            DashboardUiState(
                today = today,
                week = week,
                daysWithRecords = weekRecords.map { it.date }.toSet(),
                totals = totals,
                recentRecords = latestPerMetric(allRecords),
            )
        }.stateIn(
            scope = viewModelScope,
            // Keep listening for 5 s after the screen goes away (e.g. during rotation),
            // then stop so the database is not queried for a screen nobody is looking at.
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState(today = today, week = week),
        )

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
