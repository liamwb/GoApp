package com.goapppackage.goapp.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.goapppackage.goapp.data.settings.SettingEntity
import com.goapppackage.goapp.data.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    fun getBoardSize(): Flow<Int> {

        // cheese the weird null pointer exception that I was getting...
        // TODO: work out what the fuck is going on
        return settingsRepository.getSettingStream("boardsize").map { settingEntity ->
            if (settingEntity == null) {
                Log.e("error", "settingEntity is null")
                9
            }
            else {
                settingEntity.settingValue.toInt()
            }
        }
    }

    fun setBoardsize(newBoardsize: Int) {
        viewModelScope.launch { settingsRepository.insertSetting(SettingEntity("boardsize", newBoardsize.toString())) }
    }


    // Define ViewModel factory in a companion object
    companion object {
        // the SettingsRepository is provided to the viewModel factory. Make sure to use

        val Factory: (SettingsRepository) -> ViewModelProvider.Factory = { repository ->
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    // Create a SavedStateHandle for this ViewModel from extras
                    val savedStateHandle = extras.createSavedStateHandle()

                    return SettingsViewModel(
                        repository,
                        savedStateHandle
                    ) as T
                }
            }
        }
    }
}




