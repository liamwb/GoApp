package com.example.goapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.example.goapp.data.ScreenOrientation
import com.example.goapp.data.currentgame.CurrentGameDatabase
import com.example.goapp.data.currentgame.CurrentGameRepository
import com.example.goapp.data.settings.SettingsDatabase
import com.example.goapp.data.settings.SettingsRepository
import com.example.goapp.ui.SettingsViewModel
import com.example.goapp.ui.theme.GoAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val settingsDatabase = SettingsDatabase.getDatabase(applicationContext)
            val settingsRepository = SettingsRepository(settingsDatabase.settingDao())
            val currentGameDatabase = CurrentGameDatabase.getDatabase(applicationContext)
            val currentGameRepository = CurrentGameRepository(currentGameDatabase.currentGameDao())

            setContent {
                // do we want portrait or landscape
                val displayMetrics = LocalContext.current.resources.displayMetrics
                val width = displayMetrics.widthPixels
                val height = displayMetrics.heightPixels
                val orientation = when (width >= height) {
                    true -> ScreenOrientation.LANDSCAPE
                    false -> ScreenOrientation.PORTRAIT
                }


                GoAppTheme(
                    darkTheme = true
                ) {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        val settingsViewModel: SettingsViewModel by viewModels {
                             SettingsViewModel.Factory(settingsRepository)
                        }

                        GoApp(
                            orientation = orientation,
                            settingsViewModel = settingsViewModel,
                            currentGameRepository = currentGameRepository
                        )
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MainPreview() {
//    GoAppTheme(darkTheme = true) {
//        GoApp(ScreenOrientation.LANDSCAPE)
//    }
//}

