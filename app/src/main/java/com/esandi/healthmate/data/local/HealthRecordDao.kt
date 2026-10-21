package com.esandi.healthmate.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object: every SQL statement HealthMate runs against `health_records`.
 * Room checks these queries against the table at compile time and generates the code.
 *
 * Functions returning Flow are "live": Room re-runs them and emits new results whenever the
 * table changes, so screens update automatically after an insert, update or delete.
 * suspend functions run once, off the main thread.
 */
@Dao
interface HealthRecordDao {

    // ---------- Create ----------
    @Insert
    suspend fun insert(record: HealthRecordEntity): Long

    @Insert
    suspend fun insertAll(records: List<HealthRecordEntity>)

    // ---------- Read ----------
    @Query("SELECT * FROM health_records ORDER BY date DESC, time DESC")
    fun observeAll(): Flow<List<HealthRecordEntity>>

    @Query("SELECT * FROM health_records WHERE id = :id")
    fun observeById(id: Long): Flow<HealthRecordEntity?>

    @Query("SELECT * FROM health_records WHERE id = :id")
    suspend fun getById(id: Long): HealthRecordEntity?

    /** Search by date: all records on one day. */
    @Query("SELECT * FROM health_records WHERE date = :date ORDER BY time DESC")
    fun observeByDate(date: String): Flow<List<HealthRecordEntity>>

    /** Filter by a date range (inclusive), e.g. the last 7 or 30 days. */
    @Query("SELECT * FROM health_records WHERE date BETWEEN :from AND :to ORDER BY date DESC, time DESC")
    fun observeBetween(from: String, to: String): Flow<List<HealthRecordEntity>>

    /** Dashboard summary: totals for one day, calculated by SQLite. */
    @Query(
        """
        SELECT COALESCE(SUM(steps), 0)         AS steps,
               COALESCE(SUM(calories), 0)      AS calories,
               COALESCE(SUM(water), 0)         AS water,
               COALESCE(SUM(sleep_minutes), 0) AS sleepMinutes
        FROM health_records
        WHERE date = :date
        """,
    )
    fun observeTotalsForDate(date: String): Flow<DailyTotalsRow>

    @Query("SELECT COUNT(*) FROM health_records")
    suspend fun count(): Int

    // ---------- Update ----------
    @Update
    suspend fun update(record: HealthRecordEntity)

    // ---------- Delete ----------
    @Delete
    suspend fun delete(record: HealthRecordEntity)

    @Query("DELETE FROM health_records")
    suspend fun deleteAll()
}

/** Result of the totals query; column names match the SQL aliases above. */
data class DailyTotalsRow(
    val steps: Int,
    val calories: Int,
    val water: Int,
    val sleepMinutes: Int,
)
