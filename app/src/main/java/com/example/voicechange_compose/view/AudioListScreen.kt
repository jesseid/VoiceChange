package com.example.voicechange_compose.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AudioListScreen() {
    Column() {
        Text(text = "new Record",
        modifier = Modifier
            .fillMaxSize()
            .size(50.dp))
    }
}