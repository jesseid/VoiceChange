package com.example.voicechange_compose.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.voicechange_compose.R
import com.example.voicechange_compose.module.AudioInfo
import com.example.voicechange_compose.module.ChangeType
import com.example.voicechange_compose.module.changeTypeList
import com.example.voicechange_compose.module.homeAudioInfoList
import com.example.voicechange_compose.viewmodel.MainViewModel
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@ExperimentalFoundationApi
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    rememberSystemUiController().setStatusBarColor(Color.Transparent, darkIcons = true)
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("录音", "记录", "设置")
    Scaffold(
        modifier = Modifier.padding(0.dp,35.dp,0.dp,16.dp),
        topBar = {
            TopAppBar(
//                modifier = Modifier.padding(16.dp),
                navigationIcon = { IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.MoreVert,null)
                }},
                title = {
                    Text("主页")
                },
            )
        },
        bottomBar = {
            BottomNavigation {
                items.forEachIndexed { index, item ->
                    BottomNavigationItem(
                        icon = {
                            when(index){
                                0 -> Icon(Icons.Filled.Home, contentDescription = null)
                                1 -> Icon(Icons.Filled.List, contentDescription = null)
                                else -> Icon(Icons.Filled.Settings, contentDescription = null)
                            }
                        },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ){
        Column(Modifier) {
            Spacer(
                modifier = Modifier.statusBarsHeight()
            )


            AudioInfoList()


            ChangeTypeList()

            BottomBar(viewModel)

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
    Spacer(modifier = Modifier.size(100.dp,100.dp))
}

@Composable
fun AudioInfoList() {
    Text(
        text = "采样信息",
        color = Color.Gray,
        style = MaterialTheme.typography.h5,
        modifier = Modifier.padding(16.dp,16.dp,16.dp,8.dp)
    )
    LazyColumn {
        items(homeAudioInfoList) {
            AudioInfoItem(it)
        }
    }
}

@Composable
fun AudioInfoItem(item: AudioInfo) {
    LocalContext.current
    Box(
        modifier = Modifier
            .height(200.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Blue, Color.Green),
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
            Text(
                text = "PitchSemiTones: " + item.pitchSemiTones.toString(),
                color = Color.White,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Text(
                text = "TempoChange: " + item.tempoChange.toString(),
                color = Color.White,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Text(
                text = "SpeedChange: " + item.speedChange.toString(),
                color = Color.White,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun ChangeTypeList() {
    val tag0 = changeTypeList.slice(0..0)
    val tag1 = changeTypeList.slice(1..3)
    val tag2 = changeTypeList.slice(4..6)
    val tagList = listOf(tag0,tag1,tag2)
    val selectedTag = remember { mutableStateOf("") }
    Column() {
        tagList.forEach(){
            ChangeRow(tag = it,selectedTag)
        }

    }
}

@Composable
fun ChangeRow(tag:List<ChangeType>,selectedTag:MutableState<String>) {
    Row(
        modifier = Modifier.padding(top = 20.dp)
    ) {
        tag.forEach {
            Row(
                modifier = Modifier.padding(start = 10.dp)
            ) {
                RadioButton(
                    selected = it.name == selectedTag.value,
                    onClick = {
                        selectedTag.value = it.name
                    }
                )

                Text(
                    text = it.name,
                    modifier = Modifier.size(80.dp,50.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))
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
                .height(height = 120.dp)
                .padding(start=150.dp)
            ,
            horizontalArrangement = Arrangement.Center,
                ){
            LottieAnimation(
                lottieComposition,
                lottieAnimationState,
                modifier = Modifier
                    .size(100.dp)
                    .padding(0.dp, 40.dp, 0.dp, 0.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                alignment = Alignment.Center
            )
        }

    }
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(onClick = { /*TODO*/ },
            modifier = Modifier.padding(0.dp,10.dp,0.dp,0.dp)) {
            Text(text = "试听",)
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
        Button(onClick = {  },
            modifier = Modifier.padding(0.dp,10.dp,0.dp,0.dp)) {
            Text(text = "保存")
        }
    }
}