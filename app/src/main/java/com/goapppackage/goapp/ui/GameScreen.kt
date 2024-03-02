package com.goapppackage.goapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goapppackage.goapp.R
import com.goapppackage.goapp.data.Piece
import com.goapppackage.goapp.data.util.ScreenOrientation


@Composable
fun GameScreen(
    orientation: ScreenOrientation,
    uiState: GoUiState,
    inputMove: (Int, Int, Piece) -> Unit,
    onUndoButtonPressed: () -> Unit,
    showGameOverDialog: () -> Boolean,
    onDismissDialogRequest: () -> Unit,
    onPlayAgain: () -> Unit,
    onReturnToMenu: () -> Unit,
    onPassButtonPressed: () -> Unit,
    dialogBodyTextGenerator: () -> String,
    modifier: Modifier = Modifier,
) {
//    BackHandler {
//        navigateBackAndSaveGame()
//    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopStart
    ) {
        // Use a row or a column depending on whether the screen in landscape or portrait
        if (orientation == ScreenOrientation.LANDSCAPE) {
            LandscapeLayout(
                uiState = uiState,
                inputMove = inputMove,
                onUndoButtonPressed = onUndoButtonPressed,
                modifier = modifier,
                onPassButtonPressed = onPassButtonPressed,
            )
        } else {
            PortraitLayout(
                uiState = uiState,
                inputMove = inputMove,
                onUndoButtonPressed = onUndoButtonPressed,
                onPassButtonPressed = onPassButtonPressed,
                modifier = modifier,
                )
        }

        if (showGameOverDialog()) {
            GameOverDialogue(
                onDismissRequest = onDismissDialogRequest,
                onPlayAgain = onPlayAgain,
                onReturnToMenu = onReturnToMenu,
                bodyTextGenerator = dialogBodyTextGenerator
            )
        }
    }

}

@Composable fun LandscapeLayout (
    uiState: GoUiState,
    inputMove: (Int, Int, Piece) -> Unit,
    onUndoButtonPressed: () -> Unit,
    onPassButtonPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerComposableCard(
            name = "Player 1",
            style = MaterialTheme.typography.headlineSmall,
            orientation = ScreenOrientation.LANDSCAPE,
            onUndoButtonPressed = onUndoButtonPressed,
            onPassButtonPressed = onPassButtonPressed,
        )

        Board(
            gameState = uiState.gameState,
            activePlayer = uiState.gameState.activePlayer,
            inputMove = inputMove,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        PlayerComposableCard(
            name = "Player 2",
            style = MaterialTheme.typography.headlineSmall,
            orientation = ScreenOrientation.LANDSCAPE,
            onUndoButtonPressed = onUndoButtonPressed,
            onPassButtonPressed = onPassButtonPressed,
                    modifier = Modifier
                .rotate(180f)
        )
    }
}

@Composable fun PortraitLayout (
    uiState: GoUiState,
    inputMove: (Int, Int, Piece) -> Unit,
    onUndoButtonPressed: () -> Unit,
    onPassButtonPressed: () -> Unit,
    modifier: Modifier = Modifier,
    ) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlayerComposableCard(
            name = "Player 2",
            style = MaterialTheme.typography.displaySmall,
            orientation = ScreenOrientation.PORTRAIT,
            onUndoButtonPressed = onUndoButtonPressed,
            modifier = Modifier.rotate(180f),
            onPassButtonPressed = onPassButtonPressed,
        )

        Board(
            gameState = uiState.gameState,
            activePlayer = uiState.gameState.activePlayer,
            inputMove = inputMove,
            lineColor = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        PlayerComposableCard(
            name = "Player 1",
            style = MaterialTheme.typography.displaySmall,
            orientation = ScreenOrientation.PORTRAIT,
            onUndoButtonPressed = onUndoButtonPressed,
            onPassButtonPressed = onPassButtonPressed,
        )
    }
}

@Composable fun PlayerComposableCard(
    modifier: Modifier = Modifier,
    name: String,
    style: TextStyle,
    orientation: ScreenOrientation,
    onUndoButtonPressed: () -> Unit,
    onPassButtonPressed: () -> Unit,
) {
    if (orientation == ScreenOrientation.LANDSCAPE) {
        PlayerComposableCardColumn(
            modifier = modifier,
            name = name,
            style = style,
            onUndoButtonPressed = onUndoButtonPressed,
            onPassButtonPressed = onPassButtonPressed
        )
    } else {
        PlayerComposableCardRow(
            modifier = modifier,
            name = name,
            style = style,
            onUndoButtonPressed = onUndoButtonPressed,
            onPassButtonPressed = onPassButtonPressed
        )
    }
}

@Composable fun PlayerComposableCardColumn(
    modifier: Modifier = Modifier,
    name: String,
    style: TextStyle,
    onUndoButtonPressed: () -> Unit,
    onPassButtonPressed: () -> Unit, ) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        OutlinedCard(
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = name, style = style, modifier = Modifier.padding(8.dp))

                FilledTonalButton(onClick =  onUndoButtonPressed, modifier = Modifier.padding(8.dp) ) {
                    Icon(painter = painterResource(R.drawable.undo_48px),
                        "Undo Button")
                }

                FilledTonalButton(onClick =  onPassButtonPressed, modifier = Modifier.padding(8.dp) ) {
                    Icon(painter = painterResource(R.drawable.step_over_24px),
                        "Undo Button")
                }
            }
        }
    }
}

@Composable fun PlayerComposableCardRow(
    modifier: Modifier = Modifier,
    name: String,
    style: TextStyle,
    onUndoButtonPressed: () -> Unit,
    onPassButtonPressed: () -> Unit, ) {
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        OutlinedCard(
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = name, style = style, modifier = Modifier.padding(8.dp))

                Row{
                    FilledTonalButton(onClick =  onUndoButtonPressed, modifier = Modifier.padding(8.dp) ) {
                        Icon(painter = painterResource(R.drawable.undo_48px),
                            "Undo Button")
                    }

                    FilledTonalButton(onClick =  onPassButtonPressed, modifier = Modifier.padding(8.dp) ) {
                        Icon(painter = painterResource(R.drawable.step_over_24px),
                            "Undo Button")
                    }
                }

            }

        }
    }
}


@Composable fun GameOverDialogue(
    onDismissRequest: () -> Unit,
    onPlayAgain: () -> Unit,
    onReturnToMenu: () -> Unit,
    bodyTextGenerator: () -> String
) {
    AlertDialog(
        title = {
            Text(text = "Game Over")
        },
        text = {
            Text(text = bodyTextGenerator())
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onPlayAgain()
                }
            ) {
                Text("Play Again")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onReturnToMenu()
                }
            ) {
                Text("Menu")
            }
        }
    )
}

@Preview
@Composable
fun GameScreenPreview () {
    GameScreen(
        ScreenOrientation.PORTRAIT,
        uiState = GoUiState(), { _, _, _ ->  },
        onUndoButtonPressed = {  },
        onPassButtonPressed = {  },
        showGameOverDialog = { false },
        onDismissDialogRequest = { },
        onPlayAgain = { },
        onReturnToMenu = { },
        dialogBodyTextGenerator = {" "}
    )
}

@Preview
@Composable
fun GameOverDialoguePreview() {
    GameOverDialogue(
        onDismissRequest = {},
        onPlayAgain = {},
        onReturnToMenu = {},
        bodyTextGenerator = { "Player 1 wins" }
    )
}



