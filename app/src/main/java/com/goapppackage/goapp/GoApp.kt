package com.goapppackage.goapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.goapppackage.goapp.ui.GameOverScreen
import com.goapppackage.goapp.ui.GameScreen
import com.goapppackage.goapp.ui.GoViewModel
import com.goapppackage.goapp.ui.SettingsScreen
import com.goapppackage.goapp.ui.StartScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goapppackage.goapp.data.util.ScreenOrientation
import com.goapppackage.goapp.ui.SettingsViewModel

enum class GoAppScreen {
    Start,
    GameScreen,
    GameOverScreen,
    Settings
}

@Composable
fun GoApp(
    orientation: ScreenOrientation,
    goViewModel: GoViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val navController: NavHostController = rememberNavController()
    val uiState by goViewModel.uiState.collectAsState()
    val boardSize by settingsViewModel.getBoardSize().collectAsState(initial = 9)
    val showContinueGameButton by goViewModel.currentGameExists().collectAsState(initial = false)

    NavHost(
        navController = navController,
        startDestination = GoAppScreen.Start.name,
    )   {
        composable(route = GoAppScreen.Start.name) {
            StartScreen(
                onNewGameButtonClicked = {
                    goViewModel.makeNewGame(boardsize = boardSize)
                    navController.navigate(route = GoAppScreen.GameScreen.name)
                },
                onContinueGameButtonClicked = {
                    goViewModel.makeContinueGame()
                    navController.navigate(route = GoAppScreen.GameScreen.name)
                },
                continueGameButtonEnabled = showContinueGameButton,
                onSettingsButtonClicked = { navController.navigate(route = GoAppScreen.Settings.name) },
            )
        }

        composable(route = GoAppScreen.GameScreen.name) {
            val composableScope = rememberCoroutineScope()
            GameScreen(
                orientation = orientation,
                uiState = uiState,
                inputMove = {
                        row, col, piece -> goViewModel.checkLegalAndInputMove(row, col, piece)
                },
                onUndoButtonPressed = { goViewModel.undoMove() },
                onPassButtonPressed = { goViewModel.passTurnAndCheckGameOver() },
                showGameOverDialog = { goViewModel.showGameOverDialog() },
                onDismissDialogRequest = { goViewModel.setSeeEndGameStateTrue() },
                onPlayAgain = { goViewModel.makeNewGame() },
                onReturnToMenu = { navController.navigate(route = GoAppScreen.Start.name)},
                dialogBodyTextGenerator = { goViewModel.generateGameOverText() }
                )
        }

        composable(route = GoAppScreen.GameOverScreen.name) {
            GameOverScreen()
        }

        composable(route = GoAppScreen.Settings.name) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                onNavigateBackButtonPressed = { navController.popBackStack() }
            )
        }
    }
}

