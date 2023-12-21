package com.example.goapp.data.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey
    val settingName: String,
    val settingValue: String
)
