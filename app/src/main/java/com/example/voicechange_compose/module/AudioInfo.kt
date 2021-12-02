package com.example.voicechange_compose.module

data class AudioInfo(
    val sampleRate: Float,
    val channel: Float,
    val pitchSemiTones: Float,
    val tempoChange: Float,
    val speedChange: Float,
)

val homeAudioInfoList = listOf(
    AudioInfo(
        16000F,
        1F,
        0.0F,
        0.0F,
        0.0F,
    )

)