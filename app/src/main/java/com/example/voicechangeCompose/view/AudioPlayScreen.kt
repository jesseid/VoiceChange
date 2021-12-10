package com.example.voicechangeCompose.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.voicechangeCompose.viewmodel.MainViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun AudioPlayScreen(viewModel: MainViewModel) {

    val playState by viewModel.playState.observeAsState("")
    rememberSystemUiController().setStatusBarColor(Color.Transparent, darkIcons = true)

    Scaffold(
        modifier = Modifier.padding(0.dp,35.dp,0.dp,16.dp),
        topBar = {
            TopAppBar(
//                modifier = Modifier.padding(16.dp),
                backgroundColor = Color.White,
                navigationIcon = { IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.MoreVert,null)
                }
                },

                title = {
                    if (playState.isNotEmpty()){
                        Text(playState)
                    } else Text("new Record")
                },
            )
        },
    ) {

    }
}