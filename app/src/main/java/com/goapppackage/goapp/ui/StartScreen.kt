package com.goapppackage.goapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goapppackage.goapp.R
import com.goapppackage.goapp.ui.theme.GoAppTheme
import com.goapppackage.goapp.ui.theme.Typography

@Composable
fun StartScreen(
    onNewGameButtonClicked: () -> Unit,
    onContinueGameButtonClicked: () -> Unit,
    continueGameButtonEnabled: Boolean,
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

        Button(
            onClick = onContinueGameButtonClicked,
            enabled = continueGameButtonEnabled,
            modifier = Modifier.padding(8.dp)) {
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
        StartScreen({ }, { }, true, { })
    }
}