package com.example.voicechangeCompose.viewmodel

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.voicechangeCompose.MainActivity
import com.example.voicechangeCompose.audio.common.PlayState
import com.example.voicechangeCompose.module.AsyncResult
import com.example.voicechangeCompose.module.ChangeType
import com.example.voicechangeCompose.module.Utils
import com.example.voicechangeCompose.ui.activity.PlayActivity
import com.example.voicechangeCompose.audio.AudioEngine
import com.voicechange.audio.NetworkClient
import com.voicechange.audio.NetworkReceiver
import com.example.voicechangeCompose.audio.SampleAudioPlayer
import com.example.voicechangeCompose.audio.common.AudioParam
import com.example.voicechangeCompose.audio.common.IHandleAudioCallback
import com.voicechange.audio.common.RecordState
import com.example.voicechangeCompose.audio.common.TransFormParam
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

class MainViewModel : ViewModel(), IHandleAudioCallback {

    private var _saveState = MutableLiveData<String>()
    val saveState: LiveData<String> = _saveState

    private var _toastContent = MutableLiveData<String>()
    val toastContent: LiveData<String> = _toastContent

    private var _currentChangeType = MutableLiveData<ChangeType>()
    val currentChangeType: LiveData<ChangeType> = _currentChangeType

    private var _recordingWithPlay = MutableLiveData<Boolean>()
    private val recordingWithPlay: LiveData<Boolean> = _recordingWithPlay

    private val _isRecording = MutableLiveData<Boolean>()
    val isRecording: LiveData<Boolean> = _isRecording

    private val _showClearDialog = MutableLiveData<Boolean>()
    val showClearDialog: LiveData<Boolean> = _showClearDialog

    private val _playState = MutableLiveData<String>()
    val playState: LiveData<String> = _playState

    private val _sampleRate = MutableLiveData<String>()
    val sampleRate: LiveData<String> = _sampleRate

    private val _channel = MutableLiveData<String>()
    val channel: LiveData<String> = _channel

    private val _audioState = MutableLiveData<String>()
    val audioState: LiveData<String> = _audioState

    private var mRecordStateHandler: Handler? = null
    private var mNetworkClient: NetworkClient? = null
    private var mNetworkReceiver: NetworkReceiver? = null
    private var mAudioEngine: AudioEngine? = null

    private fun setAudioState(value: String) {
        _audioState.postValue(value)
    }

    private fun getSampleRate(): String? {
        return if (sampleRate.value != null){
            sampleRate.value
        } else "44100"
    }

    fun setSampleRate(value: String) {
        _sampleRate.postValue(value)
    }

    private fun getChannel(): String? {
        return if(channel.value != null) channel.value else "1"
    }

    fun setChannel(value: String) {
        _channel.postValue(value)
    }

    private fun setPlayState(value: String) {
        _playState.postValue(value)
    }

    fun setShowClearDialog(value: Boolean) {
        _showClearDialog.postValue(value)
    }

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
        object : Handler(Looper.myLooper()!!) {
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
            MSG_RECORD_STATE
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

    private var mAudioPlayer // 播放器
            : SampleAudioPlayer? = null

    private val mHandler = object : Handler(Looper.myLooper()!!) {
        @SuppressLint("HandlerLeak")
        override fun handleMessage(msg: Message) {
            // TODO Auto-generated method stub
            when (msg.what) {
                SampleAudioPlayer.STATE_MSG_ID -> showState(msg.obj as Int)
            }
        }
    }

    private fun initPlayLogic() {
        mAudioPlayer = SampleAudioPlayer(mHandler)
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
            setPlayState(str)
        }
    }

    @SuppressLint("SetTextI18n")
    fun play() {
        if (mAudioPlayer!!.playState == PlayState.MPS_PAUSE) {
            mAudioPlayer!!.play()
            return
        }

        // 获取音频数据
        val data = pCMData
        if (data == null) {
            setAudioState("$filePath：该路径下不存在文件！")
            return
        }

        // 获取音频参数
        val audioParam = audioParam
        mAudioPlayer!!.setAudioParam(audioParam)
        mAudioPlayer!!.setDataSource(data)

        // 音频源就绪
        mAudioPlayer!!.prepare()
        mAudioPlayer!!.play()
    }

    fun pause() {
        mAudioPlayer!!.pause()
    }

    fun stop() {
        mAudioPlayer!!.stop()
    }

    private val audioParam: AudioParam
        get() {
            val frequency = getSampleRate()
            val mFrequency = Integer.valueOf(frequency!!)
            val channel = getChannel()
            val mChannel = Integer.valueOf(channel!!)
            val audioParam = AudioParam()
            audioParam.mFrequency = mFrequency
            audioParam.mChannelConfig = if (mChannel == 1) AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO
            audioParam.mSampBitConfig = AudioFormat.ENCODING_PCM_16BIT
            return audioParam
        }

    private val filePath = Utils.localExternalPath + "/voiceChange/soundtouch.pcm"

    private val pCMData: ByteArray?
        get() {
            val file = File(filePath)
            if (!file.exists()) {
                Log.d(PlayActivity.TAG, "pcm  can't find path:$filePath")
                return null
            }
            Log.d(PlayActivity.TAG, "pcm  find path:$filePath")
            val inStream: FileInputStream = try {
                FileInputStream(file)
            } catch (e: FileNotFoundException) {
                Log.e(PlayActivity.TAG, "FileNotFoundException:" + e.message)
                // TODO Auto-generated catch block
                e.printStackTrace()
                return null
            }
            val dataPack: ByteArray
            val size = file.length()
            dataPack = ByteArray(size.toInt())
            try {
                inStream.read(dataPack)
            } catch (e: IOException) {
                Log.e(PlayActivity.TAG, "IOException:" + e.message)
                // TODO Auto-generated catch block
                e.printStackTrace()
                return null
            }
            return dataPack
        }


    init {
        initAudioEngine()
        initPlayLogic()
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