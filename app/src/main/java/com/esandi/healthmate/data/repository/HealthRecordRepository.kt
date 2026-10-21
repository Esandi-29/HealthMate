package com.esandi.healthmate.data.repository

import com.esandi.healthmate.data.local.HealthRecordDao
import com.esandi.healthmate.data.local.toEntity
import com.esandi.healthmate.data.local.toModel
import com.esandi.healthmate.data.model.DailyTotals
import com.esandi.healthmate.data.model.HealthRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * The single source of health data for the ViewModels. They work with the app model
 * (HealthRecord, LocalDate) and never see SQL, table rows or date strings - those details
 * stay in the data layer. Being an interface also lets tests swap in a fake.
 */
interface HealthRecordRepository {
    fun observeAll(): Flow<List<HealthRecord>>
    fun observeRecord(id: Long): Flow<HealthRecord?>
    fun observeByDate(date: LocalDate): Flow<List<HealthRecord>>
    fun observeBetween(from: LocalDate, to: LocalDate): Flow<List<HealthRecord>>
    fun observeTotals(date: LocalDate): Flow<DailyTotals>
    suspend fun getRecord(id: Long): HealthRecord?

    /** Saves a new record and returns its generated id. */
    suspend fun add(record: HealthRecord): Long
    suspend fun update(record: HealthRecord)
    suspend fun delete(record: HealthRecord)

    /** Puts a deleted record back with its original id (used by Undo). */
    suspend fun restore(record: HealthRecord)
}

/** Room-backed implementation. Room runs suspend DAO calls on a background thread itself. */
class RoomHealthRecordRepository(
    private val dao: HealthRecordDao,
) : HealthRecordRepository {

    override fun observeAll() = dao.observeAll().map { rows -> rows.map { it.toModel() } }

    override fun observeRecord(id: Long) = dao.observeById(id).map { it?.toModel() }

    override fun observeByDate(date: LocalDate) =
        dao.observeByDate(date.toString()).map { rows -> rows.map { it.toModel() } }

    override fun observeBetween(from: LocalDate, to: LocalDate) =
        dao.observeBetween(from.toString(), to.toString()).map { rows -> rows.map { it.toModel() } }

    override fun observeTotals(date: LocalDate) = dao.observeTotalsForDate(date.toString()).map {
        DailyTotals(steps = it.steps, calories = it.calories, waterMl = it.water, sleepMinutes = it.sleepMinutes)
    }

    override suspend fun getRecord(id: Long) = dao.getById(id)?.toModel()

    override suspend fun add(record: HealthRecord) = dao.insert(record.copy(id = 0).toEntity())

    override suspend fun update(record: HealthRecord) = dao.update(record.toEntity())

    override suspend fun delete(record: HealthRecord) = dao.delete(record.toEntity())

    override suspend fun restore(record: HealthRecord) {
        dao.insert(record.toEntity())
    }
}
