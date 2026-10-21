package com.esandi.healthmate.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esandi.healthmate.data.model.HealthRecord
import com.esandi.healthmate.data.repository.HealthRecordRepository
import com.esandi.healthmate.ui.components.MetricType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate

/** Everything the History screen shows: the active filters and the matching records grouped by day. */
data class HistoryUiState(
    val filters: HistoryFilters = HistoryFilters(),
    val groups: List<DayGroup> = emptyList(),
    val resultCount: Int = 0,
    /** True until the first database result arrives, so "No records found" does not flash. */
    val isLoading: Boolean = false,
) {
    val hasActiveFilters: Boolean get() = filters != HistoryFilters()
}

/**
 * ViewModel for History (MVVM). Holds the filter choices and shows the matching records
 * from the Room database.
 *
 * The date part of the search runs in SQLite (brief: "Search/filter records by date"):
 *  - a specific date or "Today"     -> SELECT ... WHERE date = :date
 *  - "7 days" / "30 days"           -> SELECT ... WHERE date BETWEEN :from AND :to
 *  - "All"                          -> SELECT ... ORDER BY date DESC, time DESC
 * The metric filter, sorting and grouping by day are then done by [HistoryFilter].
 * Each query is a live Flow, so added, edited or deleted records appear here immediately.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModel(
    private val repository: HealthRecordRepository,
) : ViewModel() {

    private val filters = MutableStateFlow(HistoryFilters())

    val uiState: StateFlow<HistoryUiState> =
        filters
            // When the filters change, stop the old query and start the matching new one.
            .flatMapLatest { current ->
                recordsFor(current).map { records -> build(current, records) }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HistoryUiState(isLoading = true),
            )

    /** Chooses the SQL query that matches the date filter. */
    private fun recordsFor(filters: HistoryFilters): Flow<List<HealthRecord>> {
        val range = HistoryFilter.dateRangeFor(filters, LocalDate.now())
        return when {
            range == null -> repository.observeAll()
            range.first == range.second -> repository.observeByDate(range.first)
            else -> repository.observeBetween(range.first, range.second)
        }
    }

    /** Choosing a quick range clears any specific date from the date search. */
    fun onRangeChange(range: DateRange) = filters.update { it.copy(range = range, date = null) }

    fun onDateSelected(date: LocalDate) = filters.update { it.copy(date = date) }

    fun onClearDate() = filters.update { it.copy(date = null) }

    fun onMetricChange(metric: MetricType?) = filters.update { it.copy(metric = metric) }

    fun onClearAll() = filters.update { HistoryFilters() }

    private fun build(filters: HistoryFilters, records: List<HealthRecord>): HistoryUiState {
        val matching = HistoryFilter.apply(records, filters)
        return HistoryUiState(filters = filters, groups = HistoryFilter.groupByDay(matching), resultCount = matching.size)
    }
}
