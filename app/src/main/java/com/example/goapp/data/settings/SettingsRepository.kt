package com.example.goapp.data.settings

/**
 * Repository that provides insert, update, delete, and retrieve of [SettingEntity] from a given data source.
 */
class SettingsRepository(private val settingDao: SettingDao) {
    fun getAllSettingsStream() = settingDao.getAllSettings()

    fun getSettingStream(settingName: String) = settingDao.getSetting(settingName)

    suspend fun insertSetting(settingEntity: SettingEntity) = settingDao.insertSetting(settingEntity)

    suspend fun updateSetting(settingEntity: SettingEntity) = settingDao.updateSetting(settingEntity)
}