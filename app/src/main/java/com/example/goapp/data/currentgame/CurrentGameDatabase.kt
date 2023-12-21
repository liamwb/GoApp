package com.example.goapp.data.currentgame

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CurrentGameStateStackEntity::class], version = 1, exportSchema = false)
abstract class CurrentGameDatabase: RoomDatabase() {
    abstract fun currentGameDao(): CurrentGameStateStackDao

    companion object {
        @Volatile
        private var Instance: CurrentGameDatabase? = null

        fun getDatabase(context: Context): CurrentGameDatabase {

            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    CurrentGameDatabase::class.java,
                    "settings_database")
                    .fallbackToDestructiveMigration().build().also { Instance = it }
            }
        }
    }
}