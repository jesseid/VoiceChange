package com.example.voicechange_compose

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import com.example.voicechange_compose.module.AsyncResult
import com.example.voicechange_compose.ui.theme.VoiceChange_composeTheme
import com.example.voicechange_compose.view.HomeScreen
import com.example.voicechange_compose.viewmodel.MainViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.tbruyelle.rxpermissions2.RxPermissions
import com.voicechange.audio.AudioEngine
import com.voicechange.audio.NetworkClient
import com.voicechange.audio.NetworkReceiver
import com.voicechange.audio.common.IHandleAudioCallback
import com.voicechange.audio.common.RecordState

class MainActivity : ComponentActivity(), IHandleAudioCallback {
    private val viewModel by viewModels<MainViewModel>()
    var mRecordStateHandler: Handler? = null
    var mNetworkClient: NetworkClient? = null
    var mNetworkReceiver: NetworkReceiver? = null
    var mAudioEngine: AudioEngine? = null
    private var mTvRecord: TextView? = null

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ComposeVoiceChangeUI(viewModel)
        }
        initLogic()
    }

    private fun initLogic() {
        initAudioEngine()
        requestPermission()
    }

    @SuppressLint("CheckResult")
    private fun requestPermission() {
        val rxPermission = RxPermissions(this)
        rxPermission.request(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO)
            .subscribe { granted ->
                if (granted) {
                    Log.d(TAG, "granted success")
                } else {
                    Toast.makeText(this@MainActivity, "请授予相关权限再使用该应用", Toast.LENGTH_SHORT).show()
//                    finish()
                }
            }
    }

    private fun initAudioEngine() {
        mRecordStateHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                Log.i(TAG, "msg.what = " + msg.what)
                when (msg.what) {
                    MSG_RECORD_STATE -> {
                        val ar = msg.obj as AsyncResult
                        handleRecordState(ar)
                    }
                    else -> {
                    }
                }
            }
        }
        mAudioEngine = AudioEngine()
        mAudioEngine!!.registerForRecordStateChanged(mRecordStateHandler, MSG_RECORD_STATE)
        mAudioEngine!!.registerForHandleCallback(this)
        mNetworkClient = NetworkClient()
        mNetworkReceiver = NetworkReceiver()
        mNetworkReceiver!!.init()
        mNetworkClient!!.connectNetworkService(mNetworkReceiver)
    }

    private fun handleRecordState(ar: AsyncResult) {
        when (ar.result as Int) {
            RecordState.MSG_RECORDING_START -> mTvRecord!!.text = "录音中...请插入耳机"
            RecordState.MSG_RECORDING_STOP -> mTvRecord!!.text = ""
            RecordState.MSG_RECORDING_STATE_ERROR -> mTvRecord!!.text = "录音异常"
        }
    }

    override fun onHandleStart() {}

    var value = false
    private val valueObserve = Observer<Boolean> { newValue ->
        value = newValue
    }

    override fun onHandleProcess(data: ByteArray?) {
        viewModel.recordingWithPlay.observe(this, valueObserve)
        if (value) {
            mNetworkClient!!.sendAudio(data)
        }
    }

    override fun onHandleComplete() {}

    companion object {
        private val TAG = MainActivity::class.java.name
        private const val MSG_RECORD_STATE = 0x01
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

