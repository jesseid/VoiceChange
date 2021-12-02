package com.example.voicechange_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import com.example.voicechange_compose.ui.theme.VoiceChange_composeTheme
import com.example.voicechange_compose.view.HomeScreen
import com.example.voicechange_compose.viewmodel.MainViewModel
import com.google.accompanist.insets.ProvideWindowInsets

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()


    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ComposeVoiceChangeUI(viewModel)
        }
    }
}

enum class AppState {
    Splash,
    Home
}

@ExperimentalFoundationApi
@Composable
fun ComposeVoiceChangeUI(viewModel: MainViewModel) {
    VoiceChange_composeTheme {
        ProvideWindowInsets {
            val (appState, setAppState) = remember { mutableStateOf(AppState.Splash) }

            when (appState) {
                AppState.Splash -> {
                    HomeScreen(viewModel)
                }
                AppState.Home -> {
                    HomeScreen(viewModel)
                }
            }
        }
    }

}

