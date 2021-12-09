package com.example.voicechangeCompose.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.*
import com.example.voicechangeCompose.R
import com.example.voicechangeCompose.viewmodel.MainViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MineScreen(viewModel: MainViewModel) {
    Column {
        Row(
            Modifier
                .height(60.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .padding(10.dp)
                .background(color = Color(0xFFF6F6F6)),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text("一键清理缓存",
                modifier = Modifier
                    .height(40.dp)
                    .wrapContentHeight()

            )
            Spacer(modifier = Modifier.width(100.dp))
            Icon(Icons.Filled.Delete,null,
                modifier = Modifier
                    .size(30.dp)
                    .wrapContentHeight()
                    .clickable { viewModel.setShowClearDialog(true) },
                tint = Color.Red
            )
        }
        ClearCacheDialog(viewModel)
    }

}

@DelicateCoroutinesApi
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ClearCacheDialog(viewModel: MainViewModel) = run {
    val isShow by viewModel.showClearDialog.observeAsState(false)
    if (isShow) {
        Dialog(onDismissRequest = { viewModel.setShowClearDialog(false) }) {
            var isPlaying by remember {
                mutableStateOf(true)
            }
            val speed by remember {
                mutableStateOf(1f)
            }
            val lottieComposition by rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(R.raw.loading),
            )
            val lottieAnimationState by animateLottieCompositionAsState (
                composition = lottieComposition, // 动画资源句柄
                iterations = LottieConstants.IterateForever, // 迭代次数
                isPlaying = isPlaying, // 动画播放状态
                speed = speed, // 动画速度状态
                restartOnPlay = false // 暂停后重新播放是否从头开始
            )
            Box(
                modifier = Modifier
                    .size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                    if (isPlaying) {
                        Row (
                            modifier = Modifier
                                .padding(start = 0.dp)
                            ,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ){
                            LottieAnimation(
                                lottieComposition,
                                lottieAnimationState,
                                modifier = Modifier
                                    .size(200.dp)
                                    .padding(0.dp, 0.dp, 0.dp, 0.dp),
                                alignment = Alignment.Center
                            )
                        }
                        GlobalScope.launch {
                            delay(4000)
                            if (isPlaying) {
                                isPlaying = !isPlaying
                            }
                        }
                    } else {
                        val random = (50..99).random()
                        Text(text = "内存已优化$random%")
                        Button(
                            modifier = Modifier
                                .padding(top = 100.dp)
                                .wrapContentWidth(),
                            onClick = {
                                isPlaying = !isPlaying
                                viewModel.setShowClearDialog(false)
                            },
                        ) {
                            Text(text = "确认")
                        }
                    }
                }
            }
        }
    }
}