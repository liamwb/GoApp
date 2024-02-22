package com.example.goapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onNavigateBackButtonPressed: () -> Unit
) {
    var boardsizeMenuIsExpanded by remember { mutableStateOf(false) }

    // observe values from settings database
    val boardSize: Int by settingsViewModel.getBoardSize().collectAsState(initial = 9)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ExposedDropdownMenuBox(
                expanded = boardsizeMenuIsExpanded,
                onExpandedChange = { boardsizeMenuIsExpanded = it }
            ) {
                TextField(
                    value = boardSize.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Boardsize") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = boardsizeMenuIsExpanded)
                    },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = boardsizeMenuIsExpanded,
                    onDismissRequest = { boardsizeMenuIsExpanded = false })
                {

                    DropdownMenuItem(
                        text = { Text("9x9") },
                        onClick = {
                            settingsViewModel.setBoardsize(9)
                            boardsizeMenuIsExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("13x13") },
                        onClick = {
                            settingsViewModel.setBoardsize(13)
                            boardsizeMenuIsExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("19x19") },
                        onClick = {
                            settingsViewModel.setBoardsize(19)
                            boardsizeMenuIsExpanded = false
                        }
                    )
                }
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

