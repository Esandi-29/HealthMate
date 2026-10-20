package com.esandi.healthmate.data.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * One health entry, as the rest of the app sees it. Each save in the Add Record form creates
 * one of these, usually with a single metric filled in (e.g. +250 ml of water at 10:24).
 *
 * In Phase 3 this is stored as a row of the SQLite table `health_records` (task 3.1).
 * Values of 0 mean "not recorded in this entry".
 */
data class HealthRecord(
    val id: Long,
    val date: LocalDate,
    val time: LocalTime,
    val steps: Int = 0,
    val calories: Int = 0,
    val waterMl: Int = 0,
    val sleepMinutes: Int = 0,
    val note: String? = null,
)

/** Totals for one day, adding up all of that day's records. */
data class DailyTotals(
    val steps: Int = 0,
    val calories: Int = 0,
    val waterMl: Int = 0,
    val sleepMinutes: Int = 0,
) {
    companion object {
        fun of(records: List<HealthRecord>) = DailyTotals(
            steps = records.sumOf { it.steps },
            calories = records.sumOf { it.calories },
            waterMl = records.sumOf { it.waterMl },
            sleepMinutes = records.sumOf { it.sleepMinutes },
        )
    }
}

/** Daily targets. Fixed for now; the user can change them in Profile (task 4.2). */
data class DailyGoals(
    val steps: Int = 8_000,
    val calories: Int = 500,
    val waterMl: Int = 2_000,
    val sleepMinutes: Int = 8 * 60,
)
