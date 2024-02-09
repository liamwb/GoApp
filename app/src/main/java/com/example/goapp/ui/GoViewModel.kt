package com.example.goapp.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.goapp.data.GameState
import com.example.goapp.data.IllegalMove
import com.example.goapp.data.Piece
import com.example.goapp.data.currentgame.CurrentGameRepository
import com.example.goapp.data.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class GoViewModel(
    private val currentGameRepository: CurrentGameRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(GoUiState())
    val uiState: StateFlow<GoUiState> = _uiState.asStateFlow()

    fun makeNewGame(
        boardsize: Int = 9
    ) {
        _uiState.update {
            GoUiState(
                boardsize = boardsize
            )
        }
    }


    fun getActivePlayer(): Piece {
        return uiState.value.gameState.activePlayer
    }

    private fun checkMoveIsLegal(
        row: Int,
        col: Int,
        piece: Piece
    ): IllegalMove {
        return uiState.value.gameState.checkMoveIsLegalPreliminary(row, col, piece)
    }

    private fun inputMove(newGameState: GameState) {
        val oldGameState = uiState.value.gameState

        _uiState.update { currentUiState ->
            currentUiState.copy(
                gameState = newGameState,
            )
        }
        _uiState.value.gameStateStack.addLast(oldGameState)
    }

    fun undoMove() {
        val newGameState = _uiState.value.gameStateStack.removeLastOrNull()
        if (newGameState != null) {
            _uiState.update { currentState ->
                currentState.copy(
                    gameState = newGameState
                )
            }
        }


    }

    fun checkLegalAndInputMove (row: Int, col: Int, piece: Piece) {
        val (illegalMoveObject, newGameState) =
            _uiState.value.gameState.checkLegalAndInputMove(row, col, piece)

        when(illegalMoveObject) {
            IllegalMove.NONE -> {
                inputMove(newGameState)
            }
            IllegalMove.SELFCAPTURE -> {
                Log.i("IllegalMove", "SELFCAPTURE")
            }
            IllegalMove.WRONGPLAYERTOMOVE -> {
                Log.i("IllegalMove", "WRONGPLAYERTOMOVE")
            }
            IllegalMove.KO -> {
                Log.i("IllegalMove", "KO")
            }
        }
    }

    /**
     * Navigate back and save current game.
     *
     * @param navController Nav controller
     * @param currentGameRepository Current game repository
     */
    fun navigateBackAndSaveCurrentGame(
        navController: NavHostController,
    ) {
        saveCurrentGame(currentGameRepository = currentGameRepository)

        // navigate to previous screen
        navController.popBackStack()
    }

    private fun saveCurrentGame(
        currentGameRepository: CurrentGameRepository
    ) {
        viewModelScope.launch { currentGameRepository.setGameState(_uiState.value.gameStateStack) }
    }

    companion object {
        // the GoViewModel is provided to the viewModel factory. Make sure to use

        val Factory: (CurrentGameRepository) -> ViewModelProvider.Factory = { repository ->
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    // Create a SavedStateHandle for this ViewModel from extras
                    val savedStateHandle = extras.createSavedStateHandle()

                    return GoViewModel(
                        repository,
                        savedStateHandle
                    ) as T
                }
            }
        }
    }
}