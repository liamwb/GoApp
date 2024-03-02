package com.goapppackage.goapp

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.goapppackage.goapp.data.util.ScreenOrientation
import com.goapppackage.goapp.ui.GoViewModel
import com.goapppackage.goapp.ui.SettingsViewModel
import com.goapppackage.goapp.ui.theme.GoAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsRepository = (application as MyApp).settingsRepository
        val currentGameRepository = (application as MyApp).currentGameRepository

        lifecycleScope.launch {
            setContent {
                // do we want portrait or landscape
                val displayMetrics = LocalContext.current.resources.displayMetrics
                val width = displayMetrics.widthPixels
                val height = displayMetrics.heightPixels
                val orientation = when (width >= height) {
                    true -> ScreenOrientation.LANDSCAPE
                    false -> ScreenOrientation.PORTRAIT
                }

                val settingsViewModel: SettingsViewModel by viewModels {
                    SettingsViewModel.Factory(settingsRepository)
                }

                val goViewModel: GoViewModel by viewModels {
                    GoViewModel.Factory(currentGameRepository)
                }


                GoAppTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background,
                    ) {


                        GoApp(
                            orientation = orientation,
                            settingsViewModel = settingsViewModel,
                            goViewModel = goViewModel
                        )
                    }
                }
            }

            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        //Hides the ugly action bar at the top
        actionBar?.hide()

        //Hide the status bars

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars())
                hide(WindowInsets.Type.navigationBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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


