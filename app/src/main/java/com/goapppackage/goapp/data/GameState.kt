package com.goapppackage.goapp.data

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
        private fun inputMove(
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

        /**
        first, if there is only one piece to capture then we need to consider Ko.
        the ko rule can be stated: One may not capture just one stone if that stone was
        played on the previous move and that move also captured just one stone. So, we check if
        the number of pieces captured was exactly 1. If it is, then that location gains Ko = true
        and may not be played at on the next turn. Since newGameState is not a copy of the previous
        gameState, but rather a new object, we do not have to remove the previous Ko location.
         **/


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

        /**
         * Pass turn. This function:
         *  - changes the active player
         *  - does not change the board
         * @return [GameState]
         */
        fun passTurn(): GameState {

                return GameState(
                        activePlayer = getOppositePiece(activePlayer),
                        boardSize = boardSize,
                        board = board
                )
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

                return if (row <= 0) {
                        null
                } else {
                        board[row - 1][col]
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

                return if (row >= boardSize - 1) {
                        null
                } else {
                        board[row + 1][col]
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

                return if (col <= 0) {
                        null
                } else {
                        board[row][col - 1]
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

                return if (col >= boardSize - 1) {
                        null
                } else {
                        board[row][col + 1]
                }
        }

        private fun getNeighbours(location: Location): List<Location?> {
                return listOf(
                        getLocationAbove(location),
                        getLocationBelow(location),
                        getLocationLeft(location),
                        getLocationRight(location)
                )
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

                // base case: each connected vertex is either a) in visited, b) occupied by an enemy,
                // or c) off the board

//                if (
//                        getLocationAbove(startLocation)?.piece != friendlyPlayer &&
//                        getLocationBelow(startLocation)?.piece != friendlyPlayer &&
//                        getLocationLeft(startLocation)?.piece != friendlyPlayer &&
//                        getLocationRight(startLocation)?.piece != friendlyPlayer
//                ) {
//                        return Pair(false, visited)
//                }

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

        /**
         * Calculate area score. A player's area score is the sum of
         *      - the number of empty board locations completely surrounded by their pieces
         *      - the number of pieces they have on the board
         *
         * @return
         */
        fun calculateAreaScore(): Pair<Int, Int> {
                var playerOneScore = 0
                var playerTwoScore = 0
                val visitedEmptySquares = mutableSetOf<Location>()

                for (row in board.indices) { for (col in board.indices) {
                       // firstly, if there is a piece here then add one to that player's score
                       val currentLocation = board[row][col]

                       when (currentLocation.piece) {
                               Piece.PLAYER1 -> playerOneScore++
                               Piece.PLAYER2 -> playerTwoScore++
                               /***
                                * if there is an empty location here, then we need to
                                * check if it is entirely enclosed by pieces of a single
                                * type, and update the scores accordingly.
                                */
                               Piece.EMPTY -> {
                                       val surroundedEmptyLocationScore =
                                               calculateSurroundedEmptyLocationScore(
                                                       AreaScoreMultiComponent(
                                                               startLocation = currentLocation
                                                       )
                                               )
                                       val seenPiece1 = surroundedEmptyLocationScore.seenPiece1
                                       val seenPiece2 = surroundedEmptyLocationScore.seenPiece2
                                       val connectedEmptyLocations = surroundedEmptyLocationScore.visited

                                       // update scores according to which pieces are surrounding
                                       when {
                                               seenPiece1 && !seenPiece2 -> playerOneScore++
                                               seenPiece2 && !seenPiece1 -> playerTwoScore++
                                               else -> { }
                                       }
                                       // update the set of visited squares
                                       visitedEmptySquares.addAll(connectedEmptyLocations)

                               }
                       }
               } }
               return Pair(playerOneScore, playerTwoScore)
        }

        /**
         * Finds each empty board location connected by empty board locations to startLocation, and
         * returns an [AreaScoreMultiComponent] encoding that set of empty locations, as well as
         * which player's pieces border the empty region.
         *
         * @param areaScoreMultiComponent Area score multi component encoding the start location.
         * @return [AreaScoreMultiComponent]
         */
        private fun calculateSurroundedEmptyLocationScore(
                areaScoreMultiComponent: AreaScoreMultiComponent
        ): AreaScoreMultiComponent {
                val startLocation = areaScoreMultiComponent.startLocation
                var newSeenPiece1 = areaScoreMultiComponent.seenPiece1
                var newSeenPiece2 = areaScoreMultiComponent.seenPiece2
                val visited = areaScoreMultiComponent.visited

                // this function should only be called at empty board locations
                assert(startLocation.piece == Piece.EMPTY)
                visited.add(startLocation)

                // recursive case: for each adjacent empty location
                for (l in getNeighbours(startLocation)) {
                        // check if we've visited l
                        if (l !in visited) {
                                // check if there l is empty
                                when (l?.piece) {
                                        // if l is empty then continue to search recursively
                                        Piece.EMPTY -> {
                                                val newMultiComponent = AreaScoreMultiComponent(
                                                        startLocation = l,
                                                        seenPiece1 = newSeenPiece1,
                                                        seenPiece2 = newSeenPiece2,
                                                        visited = visited
                                                )
                                                val l_score = calculateSurroundedEmptyLocationScore(
                                                        newMultiComponent
                                                )
                                                // update visited, newSeenPiece1, and newSeenPiece2
                                                visited.addAll(l_score.visited)
                                                newSeenPiece1 = l_score.seenPiece1
                                                newSeenPiece2 = l_score.seenPiece2
                                        }
                                        // we don't need to search from occupied locations, but
                                        // we do need to note which pieces we've seen
                                        Piece.PLAYER1 -> newSeenPiece1 = true
                                        Piece.PLAYER2 -> newSeenPiece2 = true
                                        // if we're off the board, do nothing
                                        null -> { }
                                }
                        }
                }

                // base case: each connected vertex is either occupied, in visited, or off the board
                return AreaScoreMultiComponent(
                        startLocation = startLocation,
                        seenPiece1 = newSeenPiece1,
                        seenPiece2 = newSeenPiece2,
                        visited = visited
                )

        }

}

/**
 * Data class for the recursive function [calculateSurroundedEmptyLocationScore].
 *
 * @property startLocation
 * @property seenPiece1
 * @property seenPiece2
 * @property visited
 * @constructor Create [AreaScoreMultiComponent]
 */
data class AreaScoreMultiComponent(val startLocation: Location,
                                   val seenPiece1: Boolean = false,
                                   val seenPiece2: Boolean = false,
                                   val visited: MutableSet<Location> = mutableSetOf<Location>())

object GameStateSerializer {
        fun serialize(gameState: GameState): String {
                return Json.encodeToString(gameState)
        }

        fun deserialize(serializedGameState: String): GameState {
                return Json.decodeFromString<GameState>(serializedGameState)
        }
}
