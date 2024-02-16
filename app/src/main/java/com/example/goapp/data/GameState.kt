package com.example.goapp.data

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

/**
 * Game state.
 *
 * @property activePlayer the player next to move
 * @property boardSize
 * @property board
 * @property locationWithKo a location to which the active player cannot play because of the Ko rule
 * @constructor Create [GameState]
 */
@Stable
@Immutable
@Serializable
data class GameState (
        val activePlayer: Piece = Piece.PLAYER1,
        val boardSize: Int = 9,
        val board: List<List<Location>> = buildList {
                for (row in 0 until boardSize) {
                        add(buildList {
                                for (col in 0 until boardSize) {
                                        add(Location(
                                                coordinate = Pair(row, col),
                                                boardSize = boardSize
                                        ))
                                }
                        })
                }
        },
        var locationWithKo: Location? = null

) {
        /**
         * Check move is legal.
         *
         * @param row Row
         * @param col Col
         * @param piece Piece
         * @return [IllegalMove] IllegalMove.NONE indicates that the move is legal.
         */
        fun checkMoveIsLegalPreliminary(
                row: Int,
                col: Int,
                piece: Piece
        ): IllegalMove {
                val proposedLocation = board[row][col]
                val enemyPiece = getOppositePiece(piece)

                // check that the correct player is trying to move
                if (this.activePlayer != piece) {
                        return IllegalMove.WRONGPLAYERTOMOVE
                }

                // check the Ko rule
                if (this.locationWithKo == proposedLocation) {
                        return IllegalMove.KO
                }

                // todo
                return IllegalMove.NONE
        }


        fun makeNewGame() : GameState {
                return GameState()
        }

        fun checkLegalAndInputMove(row: Int, col: Int, piece: Piece): Pair<IllegalMove, GameState> {
                // check all the rules except for self-capture
                val preliminaryLegality = checkMoveIsLegalPreliminary(row, col, piece)
                if (preliminaryLegality != IllegalMove.NONE) {
                        return Pair(preliminaryLegality, this)
                }

                // input the move
                val newGameState = inputMove(row, col, piece)

                // check self-capture
                val newPieceLibertyInfo = newGameState.hasLiberties(row, col)

                return if (newPieceLibertyInfo?.first == false) {
                        Pair(IllegalMove.SELFCAPTURE, this)
                } else {
                        Pair(IllegalMove.NONE, newGameState)
                }

        }

        /**
         * Input move. This function:
         *  - adds the played piece to the board at the appropriate location
         *  - removes any captured pieces
         *  - adds Ko to locations which require it
         *  It does not:
         *  - check whether or not the move is legal. This is handled by
         *  [checkMoveIsLegalPreliminary] and [checkLegalAndInputMove].
         * @param row Row
         * @param col Col
         * @param piece Piece
         * @return [GameState]
         */
        fun inputMove(
                row: Int,
                col: Int,
                piece: Piece
        ): GameState {
                val nextActivePlayer = getOppositePiece(activePlayer)

                val newBoard = buildList {
                        for (row in 0 until boardSize) {
                                add(buildList {
                                        for (col in 0 until boardSize) {
                                                add(Location(
                                                        board[row][col].piece,
                                                        board[row][col].coordinate,
                                                        boardSize
                                                ))
                                        }
                                })
                        }
                }

                val newGameState = GameState(
                        activePlayer = nextActivePlayer,
                        boardSize = boardSize,
                        board = newBoard
                )

                val location = newGameState.board[row][col]
                var numberPiecesCaptured = 0

                // adds the played piece to the board at the appropriate location
                location.piece = piece

                // removes any captured pieces
                for (adjacentLocation in listOf<Location?>(
                        newGameState.getLocationAbove(location),
                        newGameState.getLocationBelow(location),
                        newGameState.getLocationLeft(location),
                        newGameState.getLocationRight(location)
                )) {  // delete pieces as we go so we don't have to check the same group twice
                        if (adjacentLocation?.piece == nextActivePlayer) {
                                val libertyInfo = newGameState.hasLiberties(adjacentLocation)
                                if (libertyInfo?.first == false) { // if the adjacent piece has no liberties

        // first, if there is only one piece to capture then we need to consider Ko.
        // the ko rule can be stated: One may not capture just one stone if that stone was
        // played on the previous move and that move also captured just one stone. So, we check if
        // the number of pieces captured was exactly 1. If it is, then that location gains Ko = true
        // and may not be played at on the next turn.

                                        // if visited has one element
                                        if (libertyInfo.second.size == 1
                                        ){
                                                newGameState.locationWithKo = libertyInfo.second.first()
                                        }

                                        // delete the captured pieces
                                        for (captureLocation in libertyInfo.second) {
                                                captureLocation.piece = Piece.EMPTY
                                        }
                                        numberPiecesCaptured += libertyInfo.second.size
                                }
                        }
                }


                if (numberPiecesCaptured == 1) {
                        assert(newGameState.locationWithKo != null)
                }

                return newGameState
        }

        private fun addPiece(row: Int, col: Int, piece: Piece) {
                board[row][col].piece = piece
        }

        private fun removePiece(row: Int, col: Int) {
                board[row][col].piece = Piece.EMPTY
        }

        /**
         * Get location above. Returns null if there is no such location on the board.
         *
         * @param location Location
         * @return [Location] or null
         */
        private fun getLocationAbove(location: Location): Location? {
                val row = location.coordinate.first
                val col = location.coordinate.second

                if (row <= 0) {
                        return null
                } else {
                        return board[row - 1][col]
                }
        }

        /**
         * Get location below. Returns null if there is no such location on the board.
         *
         * @param location Location
         * @return [Location] or null
         */
        private fun getLocationBelow(location: Location): Location? {
                val row = location.coordinate.first
                val col = location.coordinate.second

                if (row >= boardSize - 1) {
                        return null
                } else {
                        return board[row + 1][col]
                }
        }

        /**
         * Get location left. Returns null if there is no such location on the board.
         *
         * @param location Location
         * @return [Location] or null
         */
        private fun getLocationLeft(location: Location): Location? {
                val row = location.coordinate.first
                val col = location.coordinate.second

                if (col <= 0) {
                        return null
                } else {
                        return board[row][col - 1]
                }
        }

        /**
         * Get location right. Returns null if there is no such location on the board.
         *
         * @param location Location
         * @return [Location] or null
         */
        private fun getLocationRight(location: Location): Location? {
                val row = location.coordinate.first
                val col = location.coordinate.second

                if (col >= boardSize - 1) {
                        return null
                } else {
                        return board[row][col + 1]
                }
        }

        /**
         * Has liberties. Implements recursive depth-first search, where the adjacent allied pieces
         * are considered "adjacent" for the purposes of the DFS. If a liberty is found, the pair
         * (true, {}) is returned. If no liberties are found, then the pair (false, visited) is
         * returned, where visited now contains all of the pieces which need to be removed from the
         * board, as those are the pieces which together have no liberties.
         *
         * @param startLocation Start location
         * @param visited all the allied locations already visited by the search.
         * @return (true, {}) if startLocation has >= 1 liberty and (false, visited) otherwise
         */
        private fun hasLiberties(startLocation: Location,
                                 visited: MutableSet<Location> = mutableSetOf<Location>()
        ): Pair<Boolean, MutableSet<Location>>? {
                visited.add(startLocation)
                val friendlyPlayer = startLocation.piece

                // recursion case
                // for each adjacent vertex
                for (l in listOf<Location?>(
                        getLocationAbove(startLocation),
                        getLocationBelow(startLocation),
                        getLocationLeft(startLocation),
                        getLocationRight(startLocation)
                )) {
                        //check if we've visited l
                        if (l !in visited) {
                                // check if there is a liberty at l
                                when (l?.piece) {
                                    Piece.EMPTY -> {
                                            // if there is a liberty here we can terminate our search
                                            return Pair(true, mutableSetOf<Location>())
                                    }
                                    friendlyPlayer -> {
                                            // if there is a friendly piece here then the current
                                            // piece shares its liberties; search recursively
                                            val libertyInfo_l = hasLiberties(l, visited)
                                            when (libertyInfo_l?.first) {
                                                    // we found a liberty somewhere down the track
                                                    true -> {
                                                            return Pair(
                                                                    true,
                                                                    mutableSetOf<Location>()
                                                            )
                                                    }
                                                    // we didn't find a liberty
                                                    false -> {
                                                            visited.addAll(libertyInfo_l.second)
                                                    }

                                                    else -> {
                                                            Log.e("error",
                                                                    "hasLiberties returned null")
                                                    }
                                            }

                                    }
                                    else -> {
                                            // l is an enemy piece or off the board. Do nothing
                                    }
                                }
                        }

                }

                // base case: each adjacent vertex is either a) in visited, b) occupied by an enemy,
                // or c) off the board

                if (
                        getLocationAbove(startLocation)?.piece != friendlyPlayer &&
                        getLocationBelow(startLocation)?.piece != friendlyPlayer &&
                        getLocationLeft(startLocation)?.piece != friendlyPlayer &&
                        getLocationRight(startLocation)?.piece != friendlyPlayer
                ) {
                        return Pair(false, visited)
                }

                // otherwise, we have failed to find a liberty

                return Pair(
                        false,
                        visited
                        )
        }

        /**
         * Has liberties. Overloads hasLiberties function so it can take a row/col.
         *
         * Implements recursive depth-first search, where the adjacent allied pieces
         * are considered "adjacent" for the purposes of the DFS. If a liberty is found, the pair
         * (true, {}) is returned. If no liberties are found, then the pair (false, visited) is
         * returned, where visited now contains all of the pieces which need to be removed from the
         * board, as those are the pieces which together have no liberties.
         *
         * @param row Row
         * @param col Col
         * @param visited Visited
         * @return (true, {}) if startLocation has >= 1 liberty and (false, visited) otherwise
         */
        private fun hasLiberties(row: Int, col: Int,
                                 visited: MutableSet<Location> = mutableSetOf<Location>()
        ): Pair<Boolean, MutableSet<Location>>? {
                val startLocation = board[row][col]
                return hasLiberties(startLocation, visited)
        }

        private fun getOppositePiece(piece: Piece): Piece {
                return when (piece) {
                        Piece.PLAYER1 -> Piece.PLAYER2
                        Piece.PLAYER2 -> Piece.PLAYER1
                        Piece.EMPTY -> Piece.EMPTY
                }
        }


}

object GameStateSerializer {
        fun serialize(gameState: GameState): String {
                return Json.encodeToString(gameState)
        }

        fun deserialize(serializedGameState: String): GameState {
                return Json.decodeFromString<GameState>(serializedGameState)
        }
}
