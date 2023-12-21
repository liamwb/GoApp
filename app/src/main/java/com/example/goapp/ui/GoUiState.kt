package com.example.goapp.ui

import com.example.goapp.data.GameState

data class GoUiState (
    val boardsize: Int = 9,
    val gameState: GameState = GameState(boardSize = boardsize), // todo construct board according to size settings
    val gameStateStack: ArrayDeque<GameState> = ArrayDeque()
        )
