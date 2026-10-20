package com.esandi.healthmate.data.sample

import com.esandi.healthmate.data.model.HealthRecord
import java.time.LocalDate
import java.time.LocalTime

/**
 * TEMPORARY sample records so screens can be built and checked before the database exists.
 * Dates are relative to today, so the dashboard always has "today" data.
 * Replaced by real Room data in Phase 3 (the same records become the database's dummy seed, task 3.1).
 */
object SampleData {

    fun records(today: LocalDate = LocalDate.now()): List<HealthRecord> {
        var nextId = 1L
        fun rec(daysAgo: Long, h: Int, m: Int, steps: Int = 0, kcal: Int = 0, water: Int = 0, sleep: Int = 0, note: String? = null) =
            HealthRecord(nextId++, today.minusDays(daysAgo), LocalTime.of(h, m), steps, kcal, water, sleep, note)

        return listOf(
            // Today
            rec(0, 7, 5, sleep = 451, note = "Slept well"),
            rec(0, 8, 30, water = 500),
            rec(0, 10, 24, steps = 2_000, note = "Morning walk around the park"),
            rec(0, 12, 15, water = 250),
            rec(0, 13, 40, kcal = 320, note = "Lunchtime gym session"),
            rec(0, 17, 50, steps = 2_250),
            rec(0, 18, 5, water = 450),
            // Earlier this week
            rec(1, 7, 0, sleep = 412),
            rec(1, 18, 30, steps = 7_850, kcal = 420),
            rec(1, 20, 0, water = 2_000),
            rec(2, 6, 45, sleep = 480),
            rec(2, 19, 10, steps = 5_120, kcal = 280, water = 1_750),
            rec(3, 7, 20, sleep = 395),
            rec(3, 18, 0, steps = 9_340, kcal = 510, water = 2_250, note = "Evening run"),
            rec(4, 7, 10, sleep = 430),
            rec(4, 20, 15, steps = 6_400, kcal = 350, water = 1_500),
            rec(5, 8, 0, sleep = 500),
            rec(5, 17, 45, steps = 3_900, kcal = 210, water = 1_250),
            rec(6, 7, 30, sleep = 445),
            rec(6, 19, 30, steps = 8_120, kcal = 460, water = 2_100),
        )
    }
}
