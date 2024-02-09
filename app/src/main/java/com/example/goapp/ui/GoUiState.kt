package com.example.goapp.ui

import com.example.goapp.data.GameState
import com.example.goapp.data.util.ArrayDequeSerializer
import kotlinx.serialization.Serializable

@Serializable
data class GoUiState (
    val boardsize: Int = 9,
    val gameState: GameState = GameState(boardSize = boardsize), // todo construct board according to size settings
    @Serializable(with = ArrayDequeSerializer::class)
    val gameStateStack: ArrayDeque<GameState> = ArrayDeque()
        )
