package com.example.voicechange_compose.view

import android.os.Handler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.voicechange_compose.R
import com.example.voicechange_compose.module.AudioInfo
import com.example.voicechange_compose.module.ChangeType
import com.example.voicechange_compose.module.changeTypeList
import com.example.voicechange_compose.module.homeAudioInfoList
import com.example.voicechange_compose.viewmodel.MainViewModel


@ExperimentalFoundationApi
@Composable
fun RecordingScreen(viewModel: MainViewModel) {

    Column(Modifier) {
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        AudioInfoList(viewModel)

        ChangeTypeList(viewModel)

        BottomBar(viewModel)

        Spacer(modifier = Modifier.height(30.dp))
    }

}

@Composable
fun AudioInfoList(viewModel: MainViewModel) {
    Text(
        text = "采样信息",
        color = Color.Gray,
        style = MaterialTheme.typography.h5,
        modifier = Modifier.padding(16.dp,0.dp,16.dp,8.dp)
    )
    LazyColumn {
        items(homeAudioInfoList) {
            AudioInfoItem(it, viewModel)
        }
    }
}

@Composable
fun AudioInfoItem(item: AudioInfo, viewModel: MainViewModel) {
    val changeType by viewModel.currentChangeType.observeAsState(changeTypeList[0])
    Box(
        modifier = Modifier
            .height(260.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Green, Color.Green),
                        start = Offset(0f, Float.POSITIVE_INFINITY),
                        end = Offset(Float.POSITIVE_INFINITY, 0f)
                    )
                )
                .padding(8.dp)
        ) {
            Text(
                text = "SampleRate: " + item.sampleRate.toString(),
                color = Color.White,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Text(
                text = "channel: " + item.channel.toString(),
                color = Color.White,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Row() {
                Text(
                    text = "PitchSemiTones: " ,
                    color = Color.White,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(top = 15.dp)
                )
                var text by remember{ mutableStateOf("") }
                TextField(
                    value = if(text.isEmpty()) changeType.pitchSemiTones.toString() else text,
                    onValueChange = {
                        text = it
                    },
                    textStyle = LocalTextStyle.current.copy(color = Color.White)
                )
            }
            Row() {
                Text(
                    text = "TempoChange: " ,
                    color = Color.White,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(top = 15.dp)
                )
                var text by remember{ mutableStateOf("") }
                TextField(
                    value = if(text.isEmpty()) changeType.tempoChange.toString() else text,
                    onValueChange = {
                        text = it
                    },
                    textStyle = LocalTextStyle.current.copy(color = Color.White)
                )
            }
            Row() {
                Text(
                    text = "SpeedChange: " ,
                    color = Color.White,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(top = 15.dp)
                )
                var text by remember{ mutableStateOf("") }
                TextField(
                    value = if(text.isEmpty()) changeType.speedChange.toString() else text,
                    onValueChange = {
                        text = it
                    },
                    textStyle = LocalTextStyle.current.copy(color = Color.White)
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun ChangeTypeList(viewModel: MainViewModel) {
    val tag0 = changeTypeList.slice(0..0)
    val tag1 = changeTypeList.slice(1..3)
    val tag2 = changeTypeList.slice(4..6)
    val tagList = listOf(tag0,tag1,tag2)
    val selectedTag = remember { mutableStateOf("") }
    Column() {
        tagList.forEach(){
            ChangeRow(tag = it,selectedTag,viewModel)
        }

    }
}

@Composable
fun ChangeRow(tag:List<ChangeType>, selectedTag: MutableState<String>, viewModel: MainViewModel) {
    Row(
        modifier = Modifier.padding(top = 20.dp)
    ) {
        tag.forEach {
            Row(
                modifier = Modifier.padding(start = 30.dp)
            ) {
                RadioButton(
                    selected = it.name == selectedTag.value,
                    onClick = {
                        selectedTag.value = it.name
                        viewModel.refreshChangeType(it)
                    }
                )

                Text(
                    text = it.name,
                    modifier = Modifier.size(80.dp,50.dp)
                )
            }
        }
    }
}

@Composable
fun BottomBar(viewModel: MainViewModel) {
    val isPlaying by viewModel.isRecording.observeAsState(false)
    val speed by remember {
        mutableStateOf(1f)
    }
    val lottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.recording),
    )
    val lottieAnimationState by animateLottieCompositionAsState (
        composition = lottieComposition, // 动画资源句柄
        iterations = LottieConstants.IterateForever, // 迭代次数
        isPlaying = isPlaying, // 动画播放状态
        speed = speed, // 动画速度状态
        restartOnPlay = false // 暂停后重新播放是否从头开始
    )

    if (isPlaying) {
        Row (
            modifier = Modifier
                .padding(start = 0.dp)
            ,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Spacer(modifier = Modifier.width(166.dp))
            LottieAnimation(
                lottieComposition,
                lottieAnimationState,
                modifier = Modifier
                    .size(60.dp)
                    .padding(0.dp, 0.dp, 0.dp, 0.dp),
                alignment = Alignment.Center
            )
            Spacer(modifier = Modifier.width(164.dp))
        }

    } else{
        Spacer(modifier = Modifier.size(60.dp))
    }
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val checkedState = remember { mutableStateOf(true) }
        Column(
            modifier = Modifier
                .width(60.dp)
                .padding(top = 15.dp)
        ) {
            Switch(
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                    viewModel.switchRecordingWithPlay(it)
                },
            )
            Text(text = "边录边播",
                style = MaterialTheme.typography.overline,
                maxLines = 1,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        Icon(if (isPlaying) {
            Icons.Filled.Close
        } else {
            Icons.Filled.PlayArrow
        },
            contentDescription = "播放或者暂停",
            tint = Color.Blue,
            modifier = Modifier
                .clickable { viewModel.startRecording() }
                .size(60.dp)

        )
        Column(
            modifier = Modifier.width(60.dp)
        ) {
            IconButton(onClick = { viewModel.saveToFile() },
                modifier = Modifier.padding(0.dp,0.dp,0.dp,0.dp),
            ) {
                Icon(Icons.Filled.Done,null)
            }
            Text(text = "保存",
                style = MaterialTheme.typography.overline,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 10.dp,bottom = 5.dp)
            )
        }
    }
}