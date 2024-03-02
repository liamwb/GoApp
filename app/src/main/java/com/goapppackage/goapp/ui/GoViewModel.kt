package com.goapppackage.goapp.ui

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavHostController
import com.goapppackage.goapp.data.GameState
import com.goapppackage.goapp.data.IllegalMove
import com.goapppackage.goapp.data.Piece
import com.goapppackage.goapp.data.currentgame.CurrentGameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GoViewModel(
    private val currentGameRepository: CurrentGameRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(GoUiState())
    val uiState: StateFlow<GoUiState> = _uiState.asStateFlow()
    private val seeEndGameState = mutableStateOf(false)

    fun makeNewGame(
        boardsize: Int = 9
    ) {
        _uiState.update {
            GoUiState(
                boardSize = boardsize
            )
        }
    }

    fun currentGameExists(): Flow<Boolean> {
        return currentGameRepository.isNotEmpty()
    }

    fun makeContinueGame() {
        viewModelScope.launch{
            // if the gameStateStack is empty, then this will throw a NullPointerException. Therefore,
            // this function is only available to the user when [[currentGameExists]] is true.
            val gameStateStack = currentGameRepository.getGameStateStack()

            val boardSize = gameStateStack.last().boardSize
            val goUiState = GoUiState(
                boardSize = boardSize,
                gameState = gameStateStack.last(),
                gameStateStack = gameStateStack
            )
            _uiState.update {
                goUiState
            }
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

    /**
     * Pass the turn to the other player. Checks if there have been two passes in a row. If so, the
     * game is over.
     */
    fun passTurnAndCheckGameOver() {
        if (checkConsecutivePasses()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isGameOver = true
                )}
        }
        val newGameState = _uiState.value.gameState.passTurn()
        inputMove(newGameState)
    }

    fun generateGameOverText(): String {
        val areaScore = _uiState.value.gameState.calculateAreaScore()
        val winnerText = when {
            areaScore.first > areaScore.second -> "Player 1 wins"
            areaScore.first < areaScore.second -> "Player 2 wins"
            else -> "It's a draw"
        }
        return "$winnerText (${areaScore.first} to ${areaScore.second})"
    }

    /**
     * Show the game over dialog if the game is over, and the user does not want to see the end game
     * board state.
     *
     * @return
     */
    fun showGameOverDialog(): Boolean {
        return when {
            !seeEndGameState.value && uiState.value.isGameOver -> true
            !uiState.value.isGameOver -> false
            else -> {
                Log.e("e", "isGameOver is ${uiState.value.isGameOver} and seeEndGameState is ${seeEndGameState.value}")
                false
            }
        }
    }

    fun setSeeEndGameStateTrue() {
        seeEndGameState.value = true
    }

    suspend fun updateCurrentGameDatabase() {
        currentGameRepository.setGameStateStack(_uiState.value.gameStateStack)
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
                // update uiState
                inputMove(newGameState)

                // update currentGame database
                viewModelScope.launch {
                    currentGameRepository.setGameStateStack(_uiState.value.gameStateStack)
                }
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
        viewModelScope.launch {
            currentGameRepository.setGameStateStack(_uiState.value.gameStateStack)
        }
    }

    fun calculateAreaScore() {
        val areaScore = uiState.value.gameState.calculateAreaScore()
        Log.d("area", areaScore.toString())
    }

    fun getAreaScore(): Pair<Int, Int> {
        return uiState.value.gameState.calculateAreaScore()
    }

    /**
     * Checks if the last two board states are the same. If so, then there have been two consecutive
     * passes.
     *
     * @return true if the two board states on top of the stack are identical, false otherwise
     */
    private fun checkConsecutivePasses(): Boolean {
        // if there have been less than two moves, then there can't have been two consecutive passes
        if (uiState.value.gameStateStack.size < 1) {
            return false
        }

        val currentBoard = uiState.value.gameState.board
        val previousBoard = uiState.value.gameStateStack.lastOrNull()?.board

        // check (deeply) if the two boards are the same
        for (row in currentBoard.indices) {
            for (col in currentBoard.indices) {
                if (currentBoard[row][col] != previousBoard!![row][col]) {
                    return false
                }
            }
        }
        // if no two board locations are different, then we can return true
        return true
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
                        repository
                    ) as T
                }
            }
        }
    }
}