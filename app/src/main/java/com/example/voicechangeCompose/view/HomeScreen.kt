package com.example.voicechangeCompose.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.voicechange_compose.viewmodel.MainViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@ExperimentalFoundationApi
@Composable
fun HomeScreen(viewModel: MainViewModel) {

    rememberSystemUiController().setStatusBarColor(Color.Transparent, darkIcons = true)
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("录音", "记录", "设置")
    val saveState by viewModel.saveState.observeAsState("")
    val text by viewModel.toastContent.observeAsState("")

    Scaffold(
        modifier = Modifier.padding(0.dp,35.dp,0.dp,16.dp),
        topBar = {
            TopAppBar(
//                modifier = Modifier.padding(16.dp),
                backgroundColor = Color.White,
                navigationIcon = { IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.MoreVert,null)
                }},

                title = {
                    if (saveState.isNotEmpty() || text.isNotEmpty()){
                        Text(text + " " + if (saveState.length > 4) saveState.substring(0..3) else saveState)
                    } else Text("主页")
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
        if (selectedItem == 0) {
            RecordingScreen(viewModel)
        } else if(selectedItem == 1) {
            AudioListScreen(viewModel)
        } else {
            MineScreen()
        }
    }
    Spacer(modifier = Modifier.size(100.dp,100.dp))
}

