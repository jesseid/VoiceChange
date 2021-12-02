package com.example.voicechange_compose.viewmodel

import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.IOException

class MainViewModel : ViewModel() {
    private var currentPausePositionInSong = 0
    private val _isRecording = MutableLiveData<Boolean>()
    val isRecording: LiveData<Boolean> = _isRecording
    private var mediaPlayer: MediaPlayer = MediaPlayer()


    fun startRecording(){
//        if (mediaPlayer.isPlaying) {
//            //此时处于播放状态，需要暂停音乐
//            pauseMusic()
//        } else {
//            //此时没有播放音乐，点击开始播放音乐
//            playMusic()
//        }
        if (isRecording.value == true) {
            pauseMusic()
        } else {
            playMusic()
        }
    }
    private fun playMusic() {
        //从暂停到播放
//        mediaPlayer.seekTo(currentPausePositionInSong)
//        mediaPlayer.start()
        _isRecording.postValue(true)
    }

    private fun pauseMusic() {
        /* 暂停音乐的函数*/
//        if (mediaPlayer.isPlaying) {
//            currentPausePositionInSong = mediaPlayer.currentPosition
//            mediaPlayer.pause()
//        }
        _isRecording.postValue(false)
    }
}