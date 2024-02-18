package com.example.goapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.goapp.ui.GameOverScreen
import com.example.goapp.ui.GameScreen
import com.example.goapp.ui.GoViewModel
import com.example.goapp.ui.SettingsScreen
import com.example.goapp.ui.StartScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.goapp.data.util.ScreenOrientation
import com.example.goapp.ui.SettingsViewModel

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
                onNavigateBackButtonPressed = { goViewModel.navigateBackAndSaveCurrentGame(
                    navController = navController,
                ) },
                onCalculateScorePressed = { goViewModel.calculateAreaScore().toString() }

                // todo: Implement current game database / continue game button
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

