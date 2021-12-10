package com.example.voicechangeCompose.view

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.voicechangeCompose.R
import com.example.voicechangeCompose.module.changeTypeList
import com.example.voicechangeCompose.module.homeAudioInfoList
import com.example.voicechangeCompose.viewmodel.MainViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun AudioPlayScreen(viewModel: MainViewModel) {

    val activity = LocalContext.current as Activity
    var selectedItem by remember { mutableStateOf(0) }
    val playState by viewModel.playState.observeAsState("")
    val audioState by viewModel.audioState.observeAsState("")
    val items = listOf("播放", "暂停", "停止")
    rememberSystemUiController().setStatusBarColor(Color.Transparent, darkIcons = true)

    Scaffold(
        modifier = Modifier.padding(0.dp,35.dp,0.dp,16.dp),
        topBar = {
            TopAppBar(
//                modifier = Modifier.padding(16.dp),
                backgroundColor = Color.White,
                navigationIcon = { IconButton(onClick = { activity.finish() }) {
                    Icon(Icons.Filled.ArrowBack,null)
                }
                },

                title = {
                    if (playState.isNotEmpty()){
                        Text(playState)
                    } else Text("new Record")
                },
            )
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color.White,
            ) {
                items.forEachIndexed { index, item ->
                    BottomNavigationItem(
                        icon = {
                            when(index){
                                0 -> Icon(Icons.Filled.PlayArrow, contentDescription = null)
                                1 -> Icon(painter = painterResource(id = R.drawable.ic_pause) , contentDescription = null)
                                else -> Icon(painter = painterResource(id = R.drawable.ic_stop), contentDescription = null)
                            }
                        },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            when(index) {
                                0 -> viewModel.play()
                                1 -> viewModel.pause()
                                2 -> viewModel.stop()
                            }
                        }
                    )
                }
            }
        },
    ) {
        Column {
            LazyColumn {
                items(homeAudioInfoList) {
                    PlayAudioInfoItem(viewModel)
                }
            }
            if (audioState.isNotEmpty()) {
                Text(text = audioState)
            }
        }
    }
}


@Composable
fun PlayAudioInfoItem(viewModel: MainViewModel) {

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
                        colors = listOf(Color.Black, Color.DarkGray),
                        start = Offset(0f, Float.POSITIVE_INFINITY),
                        end = Offset(Float.POSITIVE_INFINITY, 0f)
                    )
                )
                .padding(8.dp)
        ) {
            Row {
                Text(
                    text = "SampleRate: " ,
                    color = Color.White,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(top = 15.dp)
                )
                var text by remember{ mutableStateOf("") }
                TextField(
                    value = if(text.isEmpty()) "16000" else text,
                    onValueChange = {
                        text = it
                        viewModel.setSampleRate(it)
                    },
                    textStyle = LocalTextStyle.current.copy(color = Color.White)
                )
            }
            Row {
                Text(
                    text = "Channel: " ,
                    color = Color.White,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(top = 15.dp)
                )
                var text by remember{ mutableStateOf("") }
                TextField(
                    value = if(text.isEmpty()) "1" else text,
                    onValueChange = {
                        text = it
                        viewModel.setChannel(it)
                    },
                    textStyle = LocalTextStyle.current.copy(color = Color.White)
                )
            }
            Row {
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
            Row {
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
            Row {
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