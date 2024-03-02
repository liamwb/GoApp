package com.goapppackage.goapp

import android.app.Application
import com.goapppackage.goapp.data.currentgame.CurrentGameDatabase
import com.goapppackage.goapp.data.currentgame.CurrentGameRepository
import com.goapppackage.goapp.data.settings.SettingsDatabase
import com.goapppackage.goapp.data.settings.SettingsRepository

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