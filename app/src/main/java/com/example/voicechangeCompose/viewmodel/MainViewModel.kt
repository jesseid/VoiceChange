package com.example.voicechangeCompose.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.voicechangeCompose.MainActivity
import com.example.voicechangeCompose.module.AsyncResult
import com.example.voicechangeCompose.module.ChangeType
import com.example.voicechangeCompose.module.Utils
import com.voicechange.audio.AudioEngine
import com.voicechange.audio.NetworkClient
import com.voicechange.audio.NetworkReceiver
import com.voicechange.audio.common.IHandleAudioCallback
import com.voicechange.audio.common.RecordState
import com.voicechange.audio.common.TransFormParam
import java.io.File

class MainViewModel : ViewModel(), IHandleAudioCallback {

    private var _saveState = MutableLiveData<String>()
    val saveState: LiveData<String> = _saveState

    private var _toastContent = MutableLiveData<String>()
    val toastContent: LiveData<String> = _toastContent

    private var _currentChangeType = MutableLiveData<ChangeType>()
    val currentChangeType: LiveData<ChangeType> = _currentChangeType

    private var _recordingWithPlay = MutableLiveData<Boolean>()
    val recordingWithPlay: LiveData<Boolean> = _recordingWithPlay

    private val _isRecording = MutableLiveData<Boolean>()
    val isRecording: LiveData<Boolean> = _isRecording

    var mRecordStateHandler: Handler? = null
    var mNetworkClient: NetworkClient? = null
    var mNetworkReceiver: NetworkReceiver? = null
    var mAudioEngine: AudioEngine? = null

    private fun setSaveState(value: String) {
        _saveState.postValue(value)
    }

    private fun setToastContent(value: String) {
        _toastContent.postValue(value)
    }

    fun switchRecordingWithPlay(value: Boolean) {
        _recordingWithPlay.postValue((value))
    }

    fun refreshChangeType(changeType: ChangeType) {
        _currentChangeType.postValue(changeType)
    }

    fun startRecording(){
        if (isRecording.value == true) {
            pauseRecord()
        } else {
            startRecord()
        }
    }
    private fun startRecord() {
        mAudioEngine!!.start(transFormParam)
        _isRecording.postValue(true)
    }

    private fun pauseRecord() {
        mAudioEngine!!.stop()
        _isRecording.postValue(false)
    }

    private fun initAudioEngine() {
        mRecordStateHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                Log.i(TAG, "msg.what = " + msg.what)
                when (msg.what) {
                    MainActivity.MSG_RECORD_STATE -> {
                        val ar = msg.obj as AsyncResult
                        handleRecordState(ar)
                    }
                    else -> {
                    }
                }
            }
        }
        mAudioEngine = AudioEngine()
        mAudioEngine!!.registerForRecordStateChanged(mRecordStateHandler,
            MainActivity.MSG_RECORD_STATE
        )
        mAudioEngine!!.registerForHandleCallback(this)
        mNetworkClient = NetworkClient()
        mNetworkReceiver = NetworkReceiver()
        mNetworkReceiver!!.init()
        mNetworkClient!!.connectNetworkService(mNetworkReceiver)
    }

    private fun handleRecordState(ar: AsyncResult) {
        when (ar.result as Int) {
            RecordState.MSG_RECORDING_START -> setToastContent("录音中")
            RecordState.MSG_RECORDING_STOP -> setToastContent("已结束")
            RecordState.MSG_RECORDING_STATE_ERROR -> setToastContent("录音异常")
        }
    }

    fun playCacheAudio() {
        mAudioEngine!!.replayAudioCache()
    }

    private val transFormParam: TransFormParam
        get() {
            val transFormParam = TransFormParam()
            val newPitch = currentChangeType.value?.pitchSemiTones
            if (newPitch != null) {
                transFormParam.mSampleRate = newPitch.toFloat().toInt()
            }
            val newRate = currentChangeType.value?.speedChange
            if (newRate != null) {
                transFormParam.mNewRate = newRate.toFloat()
            }
            val newTempo = currentChangeType.value?.tempoChange
            if (newTempo != null) {
                transFormParam.mNewTempo =  newTempo.toFloat()
            }
            return transFormParam
        }

    fun saveToFile() {
        val filePath = Utils.localExternalPath + "/voiceChange"
        val file = File(filePath)
        if (!file.exists()) {
            file.mkdirs()
        }
        val ret = mAudioEngine!!.saveToPCMFile(Utils.localExternalPath + "/voiceChange/soundtouch.pcm")
        if (ret) {
            setSaveState("保存成功" + Utils.localExternalPath + "/soundtouch.pcm")
        } else {
            setSaveState("保存失败" + Utils.localExternalPath + "/soundtouch.pcm")
        }
        mAudioEngine!!.saveToWAVFile(Utils.localExternalPath + "/voiceChange/soundtouch.wav")
    }

   init {
        initAudioEngine()
    }

    override fun onHandleStart() {

    }

    override fun onHandleProcess(data: ByteArray?) {
        if (recordingWithPlay.value == true) {
            mNetworkClient!!.sendAudio(data)
        }
    }

    override fun onHandleComplete() {
    }

    companion object {
        private val TAG = MainViewModel::class.java.name
        const val MSG_RECORD_STATE = 0x01
    }
}