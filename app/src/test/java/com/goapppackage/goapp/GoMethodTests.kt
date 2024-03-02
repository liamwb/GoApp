package com.goapppackage.goapp

import android.util.Log
import com.goapppackage.goapp.data.Piece
import com.goapppackage.goapp.data.currentgame.CurrentGameRepository
import com.goapppackage.goapp.examplegames.sgfToMoveList
import com.goapppackage.goapp.ui.GoViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*


@OptIn(ExperimentalCoroutinesApi::class)
class GoMethodTests {
    // this nonsense handles the coroutines which might be launched during the tests. We can't do
    // things normally since the test environment doesn't have a real coroutine dispatcher.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule(UnconfinedTestDispatcher())

    private val exampleGameFilepaths = listOf(
        "src/test/java/com/example/goapp/examplegames/1968-02-13.sgf",
        "src/test/java/com/example/goapp/examplegames/1968-08-14.sgf",
        "src/test/java/com/example/goapp/examplegames/1969-10-08.sgf",
        "src/test/java/com/example/goapp/examplegames/1969-11-00.sgf",
        "src/test/java/com/example/goapp/examplegames/1969-11-19.sgf",
    )

    // we don't need to use any database methods so relaxed = true
    private val currentGameRepository = mockk<CurrentGameRepository>(relaxed = true)

    private val goViewModel = GoViewModel(currentGameRepository)


    private fun setUpAndInputGame(filepath: String): Triple<List<Triple<Piece, Int, Int>>, Float, Piece> {
        // mockk these log methods so we don't get a bunch of errors
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0

        // read the game from the sgf file
        val (moveList, komi, winner) = sgfToMoveList(filepath)

        // setup and input the game
        goViewModel.makeNewGame(19)
        for (move in moveList) {
            goViewModel.checkLegalAndInputMove(move.second, move.third, move.first)
        }

        // end the game
        goViewModel.passTurnAndCheckGameOver()
        goViewModel.passTurnAndCheckGameOver()

        return Triple(moveList, komi, winner)
    }

    @Test
    fun inputMoveAndCheckGameOver0() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        val (_, _, _) = setUpAndInputGame(exampleGameFilepaths[0])

        // check that the game is over
        assertEquals(goViewModel.uiState.value.isGameOver, true)
    }

    @Test
    fun inputMoveAndCheckWinner0() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        val (_, komi, winner) = setUpAndInputGame(exampleGameFilepaths[0])

        val score = goViewModel.getAreaScore()
        val scoreWithKomi = Pair(score.first, score.second + komi)

        val winnerFromViewmodel = when{
            scoreWithKomi.first > scoreWithKomi.second -> Piece.PLAYER1
            scoreWithKomi.first < scoreWithKomi.second -> Piece.PLAYER2
            else -> Piece.EMPTY
        }
        assertEquals(winner, winnerFromViewmodel)
    }

    @Test
    fun inputMoveAndCheckGameOver1() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0


        val (_, _, _) = setUpAndInputGame(exampleGameFilepaths[1])

        // check that the game is over
        assertEquals(goViewModel.uiState.value.isGameOver, true)
    }

    @Test
    fun inputMoveAndCheckWinner1() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        val (_, komi, winner) = setUpAndInputGame(exampleGameFilepaths[1])

        val score = goViewModel.getAreaScore()
        val scoreWithKomi = Pair(score.first, score.second + komi)

        val winnerFromViewmodel = when {
            scoreWithKomi.first > scoreWithKomi.second -> Piece.PLAYER1
            scoreWithKomi.first < scoreWithKomi.second -> Piece.PLAYER2
            else -> Piece.EMPTY
        }
        assertEquals(winner, winnerFromViewmodel)
    }
    @Test
    fun inputMoveAndCheckGameOver2() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        val (_, _, _) = setUpAndInputGame(exampleGameFilepaths[2])

        // check that the game is over
        assertEquals(goViewModel.uiState.value.isGameOver, true)
    }

    @Test
    fun inputMoveAndCheckWinner2() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        val (_, komi, winner) = setUpAndInputGame(exampleGameFilepaths[2])

        val score = goViewModel.getAreaScore()
        val scoreWithKomi = Pair(score.first, score.second + komi)

        val winnerFromViewmodel = when {
            scoreWithKomi.first > scoreWithKomi.second -> Piece.PLAYER1
            scoreWithKomi.first < scoreWithKomi.second -> Piece.PLAYER2
            else -> Piece.EMPTY
        }
        assertEquals(winner, winnerFromViewmodel)
    }

    @Test
    fun inputMoveAndCheckGameOver3() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        val (_, _, _) = setUpAndInputGame(exampleGameFilepaths[3])

        // check that the game is over
        assertEquals(goViewModel.uiState.value.isGameOver, true)
    }

    @Test
    fun inputMoveAndCheckWinner3() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        val (_, komi, winner) = setUpAndInputGame(exampleGameFilepaths[3])

        val score = goViewModel.getAreaScore()
        val scoreWithKomi = Pair(score.first, score.second + komi)

        val winnerFromViewmodel = when {
            scoreWithKomi.first > scoreWithKomi.second -> Piece.PLAYER1
            scoreWithKomi.first < scoreWithKomi.second -> Piece.PLAYER2
            else -> Piece.EMPTY
        }
        assertEquals(winner, winnerFromViewmodel)
    }

    @Test
    fun inputMoveAndCheckGameOver4() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        val (_, _, _) = setUpAndInputGame(exampleGameFilepaths[4])

        // check that the game is over
        assertEquals(goViewModel.uiState.value.isGameOver, true)
    }

    @Test
    fun inputMoveAndCheckWinner4() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        val (_, komi, winner) = setUpAndInputGame(exampleGameFilepaths[4])

        val score = goViewModel.getAreaScore()
        val scoreWithKomi = Pair(score.first, score.second + komi)

        val winnerFromViewmodel = when {
            scoreWithKomi.first > scoreWithKomi.second -> Piece.PLAYER1
            scoreWithKomi.first < scoreWithKomi.second -> Piece.PLAYER2
            else -> Piece.EMPTY
        }
        assertEquals(winner, winnerFromViewmodel)
    }
}

