package com.example.goapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.goapp.R
import com.example.goapp.data.Piece
import com.example.goapp.data.util.ScreenOrientation


@Composable
fun GameScreen(
    orientation: ScreenOrientation,
    uiState: GoUiState,
    inputMove: (Int, Int, Piece) -> Unit,
    onUndoButtonPressed: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateBackButtonPressed: () -> Unit,
    onCalculateScorePressed: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        // Use a row or a column depending on whether the screen in landscape or portrait
        // there is surely a better way to do this
        if (orientation == ScreenOrientation.LANDSCAPE) {
            Row(
                modifier = modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerComposable(
                    name = "Player 1",
                    style = MaterialTheme.typography.bodyMedium,
                    onUndoButtonPressed = onUndoButtonPressed,
                    onCalculateScorePressed = onCalculateScorePressed
                )

                Board(
                    gameState = uiState.gameState,
                    activePlayer = uiState.gameState.activePlayer,
                    inputMove = inputMove,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                PlayerComposable(
                    name = "Player 2",
                    style = MaterialTheme.typography.bodyMedium,
                    onUndoButtonPressed = onUndoButtonPressed,
                    modifier = Modifier.rotate(180f),
                    onCalculateScorePressed = onCalculateScorePressed
                )

            }
        } else {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PlayerComposable(
                    name = "Player 2",
                    style = MaterialTheme.typography.bodyMedium,
                    onUndoButtonPressed = onUndoButtonPressed,
                    modifier = Modifier.rotate(180f),
                    onCalculateScorePressed = onCalculateScorePressed
                )

                Board(
                    gameState = uiState.gameState,
                    activePlayer = uiState.gameState.activePlayer,
                    inputMove = inputMove,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                PlayerComposable(
                    name = "Player 1",
                    style = MaterialTheme.typography.bodyMedium,
                    onUndoButtonPressed = onUndoButtonPressed,
                    onCalculateScorePressed = onCalculateScorePressed
                )
            }
        }
        // Back Button
        IconButton(
            onClick = onNavigateBackButtonPressed,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back"
            )
        }
    }

}

@Composable
fun PlayerComposable(
    name: String,
    style: TextStyle,
    onUndoButtonPressed: () -> Unit,
    onCalculateScorePressed: () -> Unit,
    modifier: Modifier = Modifier) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(text = name, style = style)
        
        IconButton(onClick =  onUndoButtonPressed ) {
            Icon(painter = painterResource(R.drawable.undo_48px),
                "Undo Button")
        }

        IconButton(onClick =  onCalculateScorePressed ) {
            Icon(painter = painterResource(R.drawable.ic_launcher_foreground),
                "Undo Button")
        }
    }
}

@Preview
@Composable
fun GameScreenPreview () {
    GameScreen(
        ScreenOrientation.PORTRAIT,
        uiState = GoUiState(), { a, b, c ->  },
        onUndoButtonPressed = {},
        modifier = Modifier,
        onNavigateBackButtonPressed = { },
        onCalculateScorePressed = { },
    )
}



