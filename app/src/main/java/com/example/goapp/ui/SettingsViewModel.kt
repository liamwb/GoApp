package com.example.goapp.ui

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.goapp.data.settings.SettingEntity
import com.example.goapp.data.settings.SettingsDatabase
import com.example.goapp.data.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    fun getBoardSize(): Flow<Int> {
        return settingsRepository.getSettingStream("boardsize").map {
            settingEntity -> settingEntity.settingValue.toInt()
        }
    }

    fun setBoardsize(newBoardsize: Int) {
        viewModelScope.launch { settingsRepository.updateSetting(SettingEntity("boardsize", newBoardsize.toString())) }
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




