package com.example.voicechange_compose.module

data class ChangeType(
    val name: String,
    val pitchSemiTones: Float,
    val tempoChange: Float,
    val speedChange: Float,
)

val changeTypeList = listOf<ChangeType>(
    ChangeType(
        "自定义",
    0F,
    0F,
    0F,
    ),
    ChangeType(
        "TOM猫",
        5.0F,
        12.0F,
        10.0F,
    ),
    ChangeType(
        "尖锐",
        12.0F,
        5.0F,
        15.0F,
    ),
    ChangeType(
        "男中",
        -10.0F,
        5.0F,
        50.0F,
    ),
    ChangeType(
        "颤抖",
        -5.0F,
        30.0F,
        50.0F,
    ),
    ChangeType(
        "厚重",
        0.0F,
        -50.0F,
        100.0F,
    ),
    ChangeType(
        "未知",
        -15.0F,
        -10.0F,
        60.0F,
    ),
)