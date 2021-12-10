package com.example.voicechangeCompose.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import com.example.voicechangeCompose.audio.common.PlayState
import com.example.voicechangeCompose.view.AudioPlayScreen
import com.example.voicechangeCompose.viewmodel.MainViewModel
import com.voicechange.audio.SampleAudioPlayer


class PlayActivity: ComponentActivity(){

    private val viewModel by viewModels<MainViewModel>()

    val mHandler = object : Handler(Looper.myLooper()!!) {
        @SuppressLint("HandlerLeak")
        override fun handleMessage(msg: Message) {
            // TODO Auto-generated method stub
            when (msg.what) {
                SampleAudioPlayer.STATE_MSG_ID -> showState(msg.obj as Int)
            }
        }
    }
    fun showState(state: Int) {
        var showString = ""
        when (state) {
            PlayState.MPS_UNINIT -> showString = "MPS_UNINIT"
            PlayState.MPS_PREPARE -> showString = "MPS_PREPARE"
            PlayState.MPS_PLAYING -> showString = "MPS_PLAYING"
            PlayState.MPS_PAUSE -> showString = "MPS_PAUSE"
        }
        showState(showString)
    }

    private fun showState(str: String?) {
        if (str != null) {
            viewModel.setPlayState(str)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AudioPlayUI(viewModel)
        }
        mHandler
    }
    companion object {
        fun navigate(context: Context) {
            val intent = Intent(context, PlayActivity::class.java)
            context.startActivity(intent)
        }
    }
}

@Composable
fun AudioPlayUI(viewModel: MainViewModel) {
    AudioPlayScreen(viewModel)
}
