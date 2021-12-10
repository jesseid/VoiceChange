package com.example.voicechangeCompose.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.voicechangeCompose.ui.activity.PlayActivity
import com.example.voicechangeCompose.viewmodel.MainViewModel

@Composable
fun AudioListScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    Spacer(modifier = Modifier.height(30.dp))
    Surface(
        modifier = Modifier.padding(top = 30.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "new Record",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .height(60.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        PlayActivity.navigate(context)
                        viewModel.playCacheAudio() }
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFF6F6F6), Color(0xFFF6F6F6)),
                            start = Offset(0f, Float.POSITIVE_INFINITY),
                            end = Offset(Float.POSITIVE_INFINITY, 0f)
                        )
                    )
                    .wrapContentSize(),
                style = MaterialTheme.typography.h5.copy(color = Color.DarkGray),
            )
        }
    }
}