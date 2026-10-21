package com.esandi.healthmate.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.esandi.healthmate.data.sample.SampleData

/**
 * HealthMate's SQLite database (file "healthmate.db"), built by Room.
 *
 * Only one instance exists for the whole app (singleton), because opening a database is
 * expensive and two open connections could conflict. The schema is exported to
 * app/schemas/ so every version of the table is recorded in Git.
 */
@Database(entities = [HealthRecordEntity::class], version = 1, exportSchema = true)
abstract class HealthMateDatabase : RoomDatabase() {

    abstract fun healthRecordDao(): HealthRecordDao

    companion object {
        private const val DATABASE_NAME = "healthmate.db"

        @Volatile
        private var instance: HealthMateDatabase? = null

        fun getInstance(context: Context): HealthMateDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context.applicationContext, HealthMateDatabase::class.java, DATABASE_NAME)
                    .addCallback(SeedSampleData)
                    .build()
                    .also { instance = it }
            }
    }

    /**
     * Fills the brand-new database with a week of dummy records (brief: "Provide dummy
     * records for initial testing"). onCreate runs only once - when the database file is
     * first created - so the user's own changes are never overwritten later.
     */
    private object SeedSampleData : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            SampleData.records().map { it.toEntity() }.forEach { row ->
                db.execSQL(
                    "INSERT INTO health_records (date, steps, calories, water, sleep_minutes, time, note) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    arrayOf<Any?>(row.date, row.steps, row.calories, row.water, row.sleepMinutes, row.time, row.note),
                )
            }
        }
    }
}
