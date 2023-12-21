package com.example.goapp.data

import kotlinx.serialization.Serializable

/**
 * Data class representing a location on the board.
 *
 * @property piece - A piece object representing the the piece at this location
 * @property coordinate - a pair on integers corresponding to the (row, column) on the board,
 *                      starting at (0, 0) in the top right
 * @property boardSize - 9, 13 or 19
 * @constructor Create [Location]
 */
@Serializable
data class Location (
    var piece: Piece = Piece.EMPTY,
    val coordinate: Pair<Int, Int>,
    val boardSize: Int,
        )

enum class Piece {
    PLAYER1, PLAYER2, EMPTY
}

