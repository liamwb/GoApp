package com.example.goapp.examplegames

import android.util.Log
import com.example.goapp.data.Piece
import java.io.BufferedReader
import java.io.FileReader

/**
 * Takes an SGF file and returns a list of moves, the komi, and the winner.
 *
 * @param filepath Filepath
 * @return
 */
fun sgfToMoveList(filepath: String): Triple<List<Triple<Piece, Int, Int>>, Float, Piece> {
    var reader: BufferedReader? = null
    val moveList = mutableListOf<Triple<Piece, Int, Int>>()
    var komi = 0f
    var winner = Piece.EMPTY

    try {
        reader = BufferedReader(FileReader(filepath))
        var line: String?

        while (reader.readLine().also { line = it } != null) {
            // process each line

            // if the line starts with "KN", then it encodes the komi
            if ((line?.length ?: 0) >= 2 && line?.subSequence(0, 2) == "KM") {
                komi = line!!.split("[", "]")[1].toFloat()
            }

            // if the line starts with "RE", then it encodes the winner
            if ((line?.length ?: 0) >= 2 && line?.subSequence(0, 2) == "RE") {
                val result = line!!.split("[", "]")[1]
                winner = when {
                    result[0] == 'B' -> Piece.PLAYER1
                    result[0] == 'W' -> Piece.PLAYER2
                    else -> Piece.EMPTY // something has gone wrong if we're here
                }
            }

            // if a line starts with ";" then it encodes one or more moves
            if ((line?.length ?: 0) >= 1 && line?.subSequence(0, 1) == ";") {
                val moves = line!!.split(";")
                for (move in moves) {
                    if (move.length > 4 && (move[0] == 'W' || move[0] == 'B')) {
                        val piece  = when (move[0]) {
                            'B' -> Piece.PLAYER1
                            'W' -> Piece.PLAYER2
                            else -> Piece.EMPTY
                        }
                        val row = move[2].code - 97
                        val col = move[3].code - 97
                        moveList.add(Triple(piece, row, col))
                    }
                }
            }
        }
    } catch (e: Exception) {
        Log.e("e", e.message.toString())
    } finally {
        try{
            reader?.close()
        }
        catch (e: Exception) {
            Log.e("e", e.message.toString())
        }
    }

    return Triple(moveList, komi, winner)
}