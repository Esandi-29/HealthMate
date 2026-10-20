package com.esandi.healthmate.util

import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Small helpers that turn raw values into the text the user sees.
 * Kept in one place so every screen formats numbers and dates the same way.
 */
object Formatters {

    private val integerFormat: NumberFormat = NumberFormat.getIntegerInstance(Locale.getDefault())
    private val longDate: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.getDefault())
    private val fullDate: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())
    private val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())

    /** 7850 -> "7,850" */
    fun number(value: Int): String = integerFormat.format(value)

    /** 451 minutes -> "7h 31m" */
    fun sleep(minutes: Int): String {
        val h = minutes / 60
        val m = minutes % 60
        return when {
            h == 0 -> "${m}m"
            m == 0 -> "${h}h"
            else -> "${h}h ${m}m"
        }
    }

    /** 1200 ml -> "1.2 L"; below 1000 ml stays in ml, e.g. "750 ml" */
    fun waterShort(ml: Int): String =
        if (ml >= 1000) String.format(Locale.getDefault(), "%.1f L", ml / 1000.0) else "$ml ml"

    /** 2026-10-27 -> "Tue, 27 Oct 2026" */
    fun date(date: LocalDate): String = date.format(longDate)

    /** 2026-10-27 -> "27 October 2026" */
    fun dateFull(date: LocalDate): String = date.format(fullDate)

    /** 10:24 -> "10:24 AM" */
    fun time(time: LocalTime): String = time.format(timeFormat)

    /** "Today", "Yesterday", or e.g. "Mon, 19 Oct 2026" */
    fun relativeDay(date: LocalDate, today: LocalDate = LocalDate.now()): String = when (date) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> this.date(date)
    }
}
