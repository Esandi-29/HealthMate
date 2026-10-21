package com.esandi.healthmate.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.esandi.healthmate.data.model.HealthRecord
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * One row of the SQLite table `health_records` (brief: id (PK), date (text), steps (int),
 * calories (int), water (int, ml)), plus HealthMate's extra columns sleep_minutes, time and note.
 *
 * Dates are stored as TEXT in ISO format (yyyy-MM-dd), so SQL can sort them and use
 * BETWEEN for date ranges. An index on `date` keeps date searches fast as the table grows.
 */
@Entity(tableName = "health_records", indices = [Index(value = ["date"])])
data class HealthRecordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "steps") val steps: Int = 0,
    @ColumnInfo(name = "calories") val calories: Int = 0,
    @ColumnInfo(name = "water") val water: Int = 0,
    @ColumnInfo(name = "sleep_minutes") val sleepMinutes: Int = 0,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "note") val note: String? = null,
)

private val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/** Database row -> the model the rest of the app uses. */
fun HealthRecordEntity.toModel() = HealthRecord(
    id = id,
    date = LocalDate.parse(date),
    time = LocalTime.parse(time, TIME_FORMAT),
    steps = steps,
    calories = calories,
    waterMl = water,
    sleepMinutes = sleepMinutes,
    note = note,
)

/** App model -> database row. An id of 0 tells Room to generate a new id on insert. */
fun HealthRecord.toEntity() = HealthRecordEntity(
    id = id,
    date = date.toString(),
    steps = steps,
    calories = calories,
    water = waterMl,
    sleepMinutes = sleepMinutes,
    time = time.format(TIME_FORMAT),
    note = note?.takeIf { it.isNotBlank() },
)
