package com.example.voicechangeCompose.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import com.example.voicechangeCompose.audio.common.PlayState
import com.example.voicechangeCompose.module.Utils
import com.example.voicechangeCompose.view.AudioPlayScreen
import com.example.voicechangeCompose.viewmodel.MainViewModel
import com.voicechange.audio.SampleAudioPlayer
import com.voicechange.audio.common.AudioParam
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException


class PlayActivity: ComponentActivity(){

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AudioPlayUI(viewModel)
        }
    }


    companion object {
        fun navigate(context: Context) {
            val intent = Intent(context, PlayActivity::class.java)
            context.startActivity(intent)
        }
        const val TAG = "AudioPlayerDemoActivity"
    }
}

@Composable
fun AudioPlayUI(viewModel: MainViewModel) {
    AudioPlayScreen(viewModel)
}
