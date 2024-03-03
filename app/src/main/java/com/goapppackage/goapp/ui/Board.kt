package com.goapppackage.goapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.goapppackage.goapp.data.Location
import com.goapppackage.goapp.data.Piece
import com.goapppackage.goapp.data.Piece.EMPTY
import com.goapppackage.goapp.data.Piece.PLAYER1
import com.goapppackage.goapp.data.Piece.PLAYER2
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.goapppackage.goapp.data.GameState


@Composable
fun Board(
    gameState: GameState,
    lineColor: Color = Color.Gray,
    activePlayer: Piece,
    inputMove: (Int, Int, Piece) -> Unit,
    modifier: Modifier = Modifier,
) {
    val boardSize = gameState.boardSize
    val board = gameState.board

    LazyVerticalGrid(
        columns = GridCells.Fixed(boardSize),
        contentPadding = PaddingValues(4.dp),
        modifier = modifier
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                BorderStroke(
                    width = 1.dp,
                    color = lineColor
                )
            )
    ) {
        for (row in board.indices) {
            items(board[row]) { item ->
                BoardSquare(
                    item = item,
                    lineColor = lineColor,
                    isClickable = item.piece == EMPTY,
                    onLongClick = { /* todo */ },
                    onClick = inputMove,
                    activePlayer = activePlayer,
                    boardSize = boardSize,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoardSquare(
    item: Location,
    lineColor: Color = Color.Gray,
    lineStrokeWidth: Float = 5f,
    isClickable: Boolean,
    onLongClick: () -> Unit,
    onClick: (Int, Int, Piece) -> Unit,
    activePlayer: Piece,
    boardSize: Int,
    modifier: Modifier = Modifier
) {
    var isPreview by remember { mutableStateOf(false) }
    val row = item.coordinate.first
    val col = item.coordinate.second
    val piece = item.piece

    // we don't want to draw the stems on the edges of the board
    val drawLeft =  col > 0
    val drawRight = col < boardSize - 1
    val drawTop = row > 0
    val drawBottom = row < boardSize - 1


    Box(modifier = modifier
        .fillMaxHeight()
        .aspectRatio(1f)
        .combinedClickable(
            enabled = isClickable,
            onClickLabel = "Play a piece here",
            role = Role.Button,
            onLongClickLabel = "Piece will be played when you release",
            onLongClick = onLongClick,
            onClick = { onClick(row, col, activePlayer) }
        )
    ) {
        // Draw the lines
        Canvas(
            modifier = modifier
                .fillMaxSize()
        ) {
            // calculate where to draw to and from based on position of square on the board
            val verticalStarty = when ( drawTop ) {
                true -> 0f
                false -> this.size.height / 2
            }
            val verticalEndy = when( drawBottom ) {
                true -> this.size.height
                false -> this.size.height / 2
            }
            val horizontalStartx = when( drawLeft ) {
                true -> 0f
                false -> this.size.width / 2
            }
            val horizontalEndx = when ( drawRight ) {
                true -> this.size.width
                false -> this.size.width / 2
            }

            // draw the lines
            drawLine(
                color = lineColor,
                Offset((this.size.width - lineStrokeWidth) / 2, verticalStarty),
                Offset((this.size.width - lineStrokeWidth) / 2, verticalEndy),
                strokeWidth = lineStrokeWidth
            )

            drawLine(
                color = lineColor,
                Offset(horizontalStartx, this.size.height / 2),
                Offset(horizontalEndx, this.size.height / 2),
                strokeWidth = lineStrokeWidth
            )
        }

        // Draw the pieces
        Piece(piece = piece)
    }
}

/**
 * Draw piece.
 *
 * @param piece Piece
 * @param pieceSize Piece size: scales the radius of the piece
 * @param modifier Modifier
 */
@Composable
fun Piece(
    piece: Piece,
    pieceSize: Float = .4f,
    modifier: Modifier = Modifier,
    ) {

    val color = when (piece) {
        PLAYER1 -> Color.Black
        PLAYER2 -> Color.White
        EMPTY -> Color.Transparent
    }

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxSize()
    ) {
        drawCircle(
            color = color,
            radius = this.size.maxDimension * pieceSize
        )
    }
}



@Preview
@Composable
fun BoardPreview() {
    val gameState = GameState()
    Board(gameState, activePlayer = PLAYER1, inputMove = {a, b, c -> })
}
//
//@Preview
//@Composable
//fun SquarePreview() {
//    BoardSquare(item = Location(PLAYER1, Pair(4, 4), boardSize = 9))
//}