package com.example.goapp.data.settings

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/***
 * Defines a database for the settings, and inserts default settings the first time the database
 * is created.
 *
 * Make sure to use CoroutineScope(Dispatchers.IO).launch to launch a coroutine for performing
 * database operations on a background thread. This is crucial to prevent blocking the main thread,
 * especially during the initial database creation. I.e., call the repository/Dao methods inside
 * a viewModelScope.launch { } block.
 *
 */
@Database(entities = [SettingEntity::class], version = 1, exportSchema = false)
abstract class SettingsDatabase : RoomDatabase() {

    abstract fun settingDao(): SettingDao

    companion object {
        @Volatile
        private var Instance: SettingsDatabase? = null
        fun getDatabase(context: Context): SettingsDatabase {
            // Double-check locking to ensure thread safety
            return Instance ?: synchronized(this) {
                // Check again inside the synchronized block to prevent multiple database creations
                Instance ?: buildDatabase(context).also { Instance = it }
            }
        }

        private fun buildDatabase(context: Context): SettingsDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                SettingsDatabase::class.java,
                "settings_database"
            ).fallbackToDestructiveMigration()
                .addCallback(object: RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Insert default settings on creation
                        val defaultSettings = listOf(
                            SettingEntity("boardsize","9")
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            Instance?.settingDao()?.insertSettings(defaultSettings)
                        }
                    }
                })
                .build()
        }

    }
}