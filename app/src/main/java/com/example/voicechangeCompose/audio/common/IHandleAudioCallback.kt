package com.example.voicechangeCompose.audio.common

interface IHandleAudioCallback {
    fun onHandleStart()
    fun onHandleProcess(data: ByteArray?)
    fun onHandleComplete()
}