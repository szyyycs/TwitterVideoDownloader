package com.ycs.servicetest.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ycs.servicetest.activity.ui.theme.ServiceTestTheme

class SettingActivity : ComponentActivity() {
    val settingList: MutableList<String> by lazy {
        mutableListOf("设置下载路径", "设置彩蛋生效日期", "关于VideoDownload")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ServiceTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    LazyColumn(Modifier.fillMaxWidth()) {
                        items(settingList) { message ->
                            Greeting(message)
                        }

                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(vertical = 20.dp)) {
        Text(text = name)
    }
}

@Preview()
@Composable
fun GreetingPreview() {
    ServiceTestTheme {
        Greeting("Android")
    }
}