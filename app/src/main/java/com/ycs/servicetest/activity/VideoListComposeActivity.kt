package com.ycs.servicetest.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ycs.servicetest.activity.ui.theme.ServiceTestTheme
import com.ycs.servicetest.ui.theme.BlueMain

class VideoListComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ServiceTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainUI()
                }
            }
        }
    }
}

@Composable
fun MainUI(modifier: Modifier = Modifier) {
    var enabled by remember { mutableStateOf(true) }
    val radius: Int by animateIntAsState(targetValue = if (enabled) 0 else 20)
    val width: Float by animateFloatAsState(targetValue = if (enabled) 1f else 0.5f)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth(width)
                .height(45.dp)
                .clip(RoundedCornerShape(radius.dp))
                .background(BlueMain)
                .padding(if (enabled) 10.dp else 0.dp)
        ) {
            Text(text = "22222")
            Text(text = "weerwefwdf")
        }
        Button(onClick = {
            enabled = !enabled
        }) {
            Text(text = "点击${if (enabled) "展开" else "收起"}")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ServiceTestTheme {
        MainUI()
    }
}