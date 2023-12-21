package com.example.goapp.data.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDao {

    //  if a setting with the same key already exists, it will be replaced with the new value
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(settingEntity: SettingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settingEntityList: List<SettingEntity>)

    @Update
    suspend fun updateSetting(settingEntity: SettingEntity)

    @Query("SELECT * from settings WHERE settingName = :name")
    fun getSetting(name: String): Flow<SettingEntity>

    @Query("SELECT * from settings")
    fun getAllSettings(): Flow<List<SettingEntity>>

    @Query("SELECT COUNT(*) FROM settings")
    fun getSettingsCount(): Int
}