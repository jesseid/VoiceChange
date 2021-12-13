package com.example.voicechangeCompose.audio

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.example.voicechangeCompose.audio.common.AudioConstants
import com.example.voicechangeCompose.audio.common.TransFormParam
import com.example.voicechangeCompose.module.AsyncResult
import com.example.voicechangeCompose.module.RegistrantList
import com.example.voicechangeCompose.viewmodel.MainViewModel
import com.voicechange.audio.HandleAudioClient
import com.voicechange.audio.SampleAudioPlayer
import com.voicechange.audio.common.*
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class AudioEngine : Handler(Looper.myLooper()!!), IHandleAudioCallback {
    @Volatile
    private var mStartFlag = false
    private var mRecordAudioClient: RecordAudioClient? = null
    private var mHandleAudioClient: HandleAudioClient? = null
    private val mRecordQueue: BlockingQueue<ShortArray> = LinkedBlockingQueue()
    private val mAudioResultData = LinkedList<ByteArray>()
    private lateinit var mAudioByteData: ByteArray
    private var mAudioDataSize = 0
    private val mRecordStateChangeRegistrants: RegistrantList = RegistrantList()

    @Volatile
    private var mHandleAudioRegistrants: IHandleAudioCallback? = null
    private val mAudioPlay = SampleAudioPlayer()
    private fun init() {
        mRecordAudioClient = RecordAudioClient()
        mHandleAudioClient = HandleAudioClient()
        mRecordAudioClient!!.registerForRecordStateChanged(this, MSG_RECORD_STATE)
        mHandleAudioClient!!.registerForHandleCallback(this)
    }

    fun unregisterForRecordStateChanged(h: Handler?) {
        if (h != null) {
            mRecordStateChangeRegistrants.remove(h)
        }
    }

    fun registerForRecordStateChanged(h: Handler?, what: Int) {
        if (h != null) {
            mRecordStateChangeRegistrants.addUnique(h, what, null)
        }
    }

    fun unRegisterForHandleCallback() {
        mHandleAudioRegistrants = null
    }

    fun registerForHandleCallback(callback: MainViewModel) {
        mHandleAudioRegistrants = callback
    }

    fun start(transFormParam: TransFormParam?) {
        if (mStartFlag) {
            return
        }
        mRecordQueue.clear()
        mAudioResultData.clear()
        mAudioDataSize = 0
        mRecordAudioClient!!.startRecord(mRecordQueue)
        if (transFormParam != null) {
            mHandleAudioClient!!.startHandleAudio(mRecordQueue, mAudioResultData, transFormParam)
        }
        mStartFlag = true
    }

    fun stop() {
        if (!mStartFlag) {
            return
        }
        mRecordAudioClient!!.stopRecord()
        mHandleAudioClient!!.stopHandleAudio()
        mStartFlag = false
    }

    fun replayAudioCache(): Boolean {
        if (mAudioResultData.size == 0) {
            return false
        }
        val audioParam = AudioParam()
        audioParam.mFrequency = FREQUENCY
        audioParam.mChannelConfig = CHANNEL
        audioParam.mSampBitConfig = ENCODING
        mAudioPlay.setAudioParam(audioParam)
        mAudioPlay.prepare()
        mAudioPlay.setDataSource(mAudioByteData)
        return mAudioPlay.play()
    }

    fun stopReplayAudioCache(): Boolean {
        return mAudioPlay.release()
    }

    fun saveToWAVFile(filePath: String?): Boolean {
        if (mAudioDataSize == 0) {
            return false
        }
        try {
            val header = WaveHeader(mAudioDataSize)
            val headers = header.header

            // 保存文件
            val out = FileOutputStream(filePath)
            out.write(headers)
            out.write(mAudioByteData)
            out.close()
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            return false
        } finally {
        }
        return true
    }

    fun saveToPCMFile(filePath: String?): Boolean {
        if (mAudioDataSize == 0) {
            return false
        }
        try {
            // 保存文件
            val out = FileOutputStream(filePath)
            out.write(mAudioByteData)
            out.close()
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            return false
        } finally {
        }
        return true
    }

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            MSG_RECORD_STATE -> {
                val ar: AsyncResult = msg.obj as AsyncResult
                handleRecordState(ar)
            }
        }
    }

    private fun handleRecordState(ar: AsyncResult) {
        val recordState = ar.result as Int
        Log.d(TAG, "handleRecordState recordState = $recordState")
        mRecordStateChangeRegistrants.notifyResult(recordState)
    }

    override fun onHandleStart() {
        Log.d(TAG, "onHandleStart")
        if (mHandleAudioRegistrants != null) {
            mHandleAudioRegistrants!!.onHandleStart()
        }
    }

    override fun onHandleProcess(data: ByteArray?) {
        Log.d(TAG, "onHandleProcess")
        mAudioDataSize += data!!.size
        if (mHandleAudioRegistrants != null) {
            mHandleAudioRegistrants!!.onHandleProcess(data)
        }
    }

    override fun onHandleComplete() {
        Log.d(TAG, "onHandleComplete")
        mAudioByteData = ByteArray(mAudioDataSize)
        var offset = 0
        for (bytes in mAudioResultData) {
            System.arraycopy(bytes, 0, mAudioByteData, offset, bytes.size)
            offset += bytes.size
        }
        Log.d(TAG, "mAudioByteData.size = $mAudioDataSize")
        if (mHandleAudioRegistrants != null) {
            mHandleAudioRegistrants!!.onHandleComplete()
        }
    }

    companion object {
        private val TAG = AudioEngine::class.java.name
        private const val MSG_RECORD_STATE = 0x01
        private const val FREQUENCY = AudioConstants.FREQUENCY
        private const val CHANNEL = AudioConstants.PLAY_CHANNEL
        private const val ENCODING = AudioConstants.ENCODING
    }

    init {
        init()
    }
}