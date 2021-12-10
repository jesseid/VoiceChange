package com.example.voicechangeCompose.audio.common

import com.voicechange.audio.common.AudioConstans

class TransFormParam {
    @JvmField
    var mSampleRate = AudioConstans.FREQUENCY
    @JvmField
    var mChannel = 1
    @JvmField
    var mNewPitch = 0
    @JvmField
    var mNewRate = 0f
    @JvmField
    var mNewTempo = 0f
}