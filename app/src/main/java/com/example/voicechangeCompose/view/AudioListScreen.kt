package com.example.voicechangeCompose.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.voicechangeCompose.viewmodel.MainViewModel

@Composable
fun AudioListScreen(viewModel: MainViewModel) {
    Spacer(modifier = Modifier.height(30.dp))
    Surface(
        modifier = Modifier.padding(top = 30.dp),
        color = Color.White
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "new Record",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable { viewModel.playCacheAudio() }
                    .wrapContentWidth(),
                style = MaterialTheme.typography.h5,
            )
        }
    }
}