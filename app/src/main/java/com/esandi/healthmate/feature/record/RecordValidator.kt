package com.esandi.healthmate.feature.record

import com.esandi.healthmate.util.Formatters
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

/**
 * Form validation rules for a health record (brief: "Implement form validation and error handling").
 *
 * Plain Kotlin with no Android code, so every rule can be unit-tested (task 3.6).
 * Each function returns null when the input is valid, or a message telling the user how to fix it.
 */
object RecordValidator {

    const val MAX_STEPS = 100_000
    const val MAX_CALORIES = 10_000
    const val MAX_WATER_ML = 10_000
    const val MAX_SLEEP_MINUTES = 16 * 60
    const val MAX_NOTE_LENGTH = 200

    fun validateSteps(text: String): String? =
        validateNumber(text, max = MAX_STEPS, emptyMessage = "Enter the number of steps", unit = "steps")

    fun validateCalories(text: String): String? =
        validateNumber(text, max = MAX_CALORIES, emptyMessage = "Enter the calories burned", unit = "kcal")

    fun validateWater(text: String): String? =
        validateNumber(text, max = MAX_WATER_ML, emptyMessage = "Choose or enter an amount of water", unit = "ml")

    fun validateSleep(bedtime: LocalTime, wakeTime: LocalTime): String? {
        if (bedtime == wakeTime) return "Bedtime and wake-up time can't be the same"
        val minutes = sleepMinutes(bedtime, wakeTime)
        return if (minutes > MAX_SLEEP_MINUTES) "That's more than 16 hours of sleep - please check the times" else null
    }

    fun validateDate(date: LocalDate, today: LocalDate = LocalDate.now()): String? =
        if (date.isAfter(today)) "A record can't be in the future" else null

    fun validateNote(note: String): String? =
        if (note.length > MAX_NOTE_LENGTH) "Keep the note under $MAX_NOTE_LENGTH characters" else null

    /** Minutes between bedtime and wake-up; a wake-up time earlier than bedtime means the next morning. */
    fun sleepMinutes(bedtime: LocalTime, wakeTime: LocalTime): Int {
        val minutes = Duration.between(bedtime, wakeTime).toMinutes().toInt()
        return if (minutes <= 0) minutes + 24 * 60 else minutes
    }

    private fun validateNumber(text: String, max: Int, emptyMessage: String, unit: String): String? {
        if (text.isBlank()) return emptyMessage
        val value = text.toIntOrNull() ?: return "Use numbers only"
        return when {
            value <= 0 -> "The amount must be more than 0"
            value > max -> "That's more than ${Formatters.number(max)} $unit - please check the number"
            else -> null
        }
    }
}
