package com.goapppackage.goapp.ui

import com.goapppackage.goapp.data.GameState
import com.goapppackage.goapp.data.util.ArrayDequeSerializer
import kotlinx.serialization.Serializable


/**
 * A uiState Class
 *
 * @property gameState the current game state. While it would be more efficient to just store the
 * [gameStateStack], it seems that in order for recomposition to trigger when the game state changes,
 * a new [GameState] object must be created and assigned to the [gameState] property. I'm guessing
 * this is because appending to the existing [gameStateStack] does not create a new pointer, since
 * the [gameStateStack] object itself is not being replaced.
 * @property gameStateStack the stack of game states. This is used to implement the "undo" feature.
 * @constructor Create empty Go ui state. By default, creates an empty board of size 9.
 */
@Serializable
data class GoUiState (
    val boardSize: Int = 9,
    val gameState: GameState = GameState(boardSize = boardSize),
    @Serializable(with = ArrayDequeSerializer::class)
    val gameStateStack: ArrayDeque<GameState> = ArrayDeque(),
    val isGameOver: Boolean = false
        )
