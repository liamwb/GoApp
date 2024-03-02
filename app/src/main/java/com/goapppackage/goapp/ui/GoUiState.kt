package com.goapppackage.goapp.ui

import com.goapppackage.goapp.data.GameState
import com.goapppackage.goapp.data.util.ArrayDequeSerializer
import kotlinx.serialization.Serializable

@Serializable
data class GoUiState (
    val boardSize: Int = 9,
    val gameState: GameState = GameState(boardSize = boardSize),
    @Serializable(with = ArrayDequeSerializer::class)
    val gameStateStack: ArrayDeque<GameState> = ArrayDeque(),
    val isGameOver: Boolean = false
        )
