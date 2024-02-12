package com.example.goapp

import android.app.Application
import com.example.goapp.data.currentgame.CurrentGameDatabase
import com.example.goapp.data.currentgame.CurrentGameRepository
import com.example.goapp.data.settings.SettingsDatabase
import com.example.goapp.data.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApp: Application() {
    private lateinit var settingsDatabase: SettingsDatabase
    private lateinit var currentGameDatabase: CurrentGameDatabase

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(settingsDatabase.settingDao())
    }

    val currentGameRepository: CurrentGameRepository by lazy {
        CurrentGameRepository(currentGameDatabase.currentGameDao())
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize the databases
        settingsDatabase = SettingsDatabase.getDatabase(applicationContext)
        currentGameDatabase = CurrentGameDatabase.getDatabase(applicationContext)
    }
}