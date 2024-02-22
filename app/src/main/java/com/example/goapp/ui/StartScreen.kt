package com.example.goapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.goapp.GoAppScreen
import com.example.goapp.R
import com.example.goapp.ui.theme.GoAppTheme
import com.example.goapp.ui.theme.Typography

@Composable
fun StartScreen(
    onNewGameButtonClicked: () -> Unit,
    onContinueGameButtonClicked: () -> Unit,
    onSettingsButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Button(onClick = onNewGameButtonClicked, modifier = Modifier.padding(8.dp)) {
            Text(
                text = stringResource(R.string.new_game_button_text),
                style = Typography.displayMedium
            )
        }

        Button(onClick = onContinueGameButtonClicked, modifier = Modifier.padding(8.dp)) {
            Text(
                text = stringResource(R.string.continue_game_button_text),
                style = Typography.displayMedium
            )
        }

        Button(onClick = onSettingsButtonClicked, modifier = Modifier.padding(8.dp)) {
            Text(
                text = stringResource(R.string.settings_button_text),
                style = Typography.displayMedium
            )
        }
    }
}

@Preview
@Composable
fun StartScreenPreview() {
    GoAppTheme(darkTheme = true) {
        StartScreen({ }, { }, { })
    }
}