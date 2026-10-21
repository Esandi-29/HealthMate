package com.esandi.healthmate.feature.history

import com.esandi.healthmate.data.model.HealthRecord
import com.esandi.healthmate.ui.components.MetricType
import com.esandi.healthmate.ui.components.primaryMetric
import java.time.LocalDate

/** Quick date ranges in the History segmented control. */
enum class DateRange(val label: String) {
    ALL("All"),
    TODAY("Today"),
    WEEK("7 days"),
    MONTH("30 days"),
}

/** The current History filters. A specific [date] (from the date search) overrides [range]. */
data class HistoryFilters(
    val range: DateRange = DateRange.ALL,
    val date: LocalDate? = null,
    val metric: MetricType? = null,
)

/** All records from one day, shown as one group in the list. */
data class DayGroup(val date: LocalDate, val records: List<HealthRecord>)

/**
 * Search and filter logic for History (brief: "Search/filter records by date").
 * Plain Kotlin with no Android code, so it can be unit-tested (task 3.6).
 */
object HistoryFilter {

    /** Records matching the filters, newest first. */
    fun apply(records: List<HealthRecord>, filters: HistoryFilters, today: LocalDate = LocalDate.now()): List<HealthRecord> =
        records
            .filter { matchesDate(it.date, filters, today) }
            .filter { filters.metric == null || it.primaryMetric() == filters.metric }
            .sortedWith(compareByDescending<HealthRecord> { it.date }.thenByDescending { it.time })

    /** Groups already-sorted records by day, keeping the newest day first. */
    fun groupByDay(records: List<HealthRecord>): List<DayGroup> =
        records.groupBy { it.date }.map { (date, dayRecords) -> DayGroup(date, dayRecords) }

    /**
     * The first and last day (inclusive) that the date filter allows, used to build the SQL query.
     * Null means no date limit ("All"). A specific date overrides the quick range.
     */
    fun dateRangeFor(filters: HistoryFilters, today: LocalDate = LocalDate.now()): Pair<LocalDate, LocalDate>? {
        filters.date?.let { return it to it }
        return when (filters.range) {
            DateRange.ALL -> null
            DateRange.TODAY -> today to today
            DateRange.WEEK -> today.minusDays(6) to today
            DateRange.MONTH -> today.minusDays(29) to today
        }
    }

    private fun matchesDate(date: LocalDate, filters: HistoryFilters, today: LocalDate): Boolean {
        filters.date?.let { return date == it }
        return when (filters.range) {
            DateRange.ALL -> true
            DateRange.TODAY -> date == today
            DateRange.WEEK -> !date.isBefore(today.minusDays(6)) && !date.isAfter(today)
            DateRange.MONTH -> !date.isBefore(today.minusDays(29)) && !date.isAfter(today)
        }
    }
}
