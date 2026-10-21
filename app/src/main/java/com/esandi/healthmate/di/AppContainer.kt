package com.esandi.healthmate.di

import android.content.Context
import com.esandi.healthmate.data.local.HealthMateDatabase
import com.esandi.healthmate.data.repository.HealthRecordRepository
import com.esandi.healthmate.data.repository.RoomHealthRecordRepository

/**
 * Holds the objects that live for the whole lifetime of the app and are shared by
 * every screen: the Room database and the HealthRecord repository.
 *
 * Keeping them in one place (manual dependency injection) means each ViewModel asks the
 * container for what it needs instead of building it itself.
 */
interface AppContainer {
    val healthRecordRepository: HealthRecordRepository
}

class DefaultAppContainer(
    private val context: Context,
) : AppContainer {

    /** "by lazy": the database is opened the first time something needs it, not at app start. */
    private val database: HealthMateDatabase by lazy { HealthMateDatabase.getInstance(context) }

    override val healthRecordRepository: HealthRecordRepository by lazy {
        RoomHealthRecordRepository(database.healthRecordDao())
    }
}
