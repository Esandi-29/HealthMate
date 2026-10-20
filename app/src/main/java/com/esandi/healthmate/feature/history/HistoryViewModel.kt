package com.esandi.healthmate.feature.history

import androidx.lifecycle.ViewModel
import com.esandi.healthmate.data.model.HealthRecord
import com.esandi.healthmate.data.sample.SampleData
import com.esandi.healthmate.ui.components.MetricType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

/** Everything the History screen shows: the active filters and the matching records grouped by day. */
data class HistoryUiState(
    val filters: HistoryFilters = HistoryFilters(),
    val groups: List<DayGroup> = emptyList(),
    val resultCount: Int = 0,
) {
    val hasActiveFilters: Boolean get() = filters != HistoryFilters()
}

/**
 * ViewModel for History (MVVM). Holds the filter choices and recalculates the list whenever
 * one changes. Records come from [SampleData] for now; in Phase 3 (task 3.4) they come from
 * the Room database, where the date filter can also run as a SQL query.
 */
class HistoryViewModel : ViewModel() {

    private val allRecords: List<HealthRecord> = SampleData.records()

    private val _uiState = MutableStateFlow(build(HistoryFilters()))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    /** Choosing a quick range clears any specific date from the date search. */
    fun onRangeChange(range: DateRange) = updateFilters { it.copy(range = range, date = null) }

    fun onDateSelected(date: LocalDate) = updateFilters { it.copy(date = date) }

    fun onClearDate() = updateFilters { it.copy(date = null) }

    fun onMetricChange(metric: MetricType?) = updateFilters { it.copy(metric = metric) }

    fun onClearAll() = updateFilters { HistoryFilters() }

    private fun updateFilters(change: (HistoryFilters) -> HistoryFilters) =
        _uiState.update { build(change(it.filters)) }

    private fun build(filters: HistoryFilters): HistoryUiState {
        val matching = HistoryFilter.apply(allRecords, filters)
        return HistoryUiState(filters = filters, groups = HistoryFilter.groupByDay(matching), resultCount = matching.size)
    }
}
