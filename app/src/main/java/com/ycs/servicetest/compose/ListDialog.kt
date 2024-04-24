package com.ycs.servicetest.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.ycs.servicetest.activity.AddBirthDayDialogContent

/**
 * Created on 2024/04/17.
 * @author carsonyang
 */
@Composable
fun AddMoreDialog(openDialog: MutableState<Boolean>, list: MutableList<String>) {
    val value by animateFloatAsState(
        targetValue = 1f,
        animationSpec = keyframes {
            durationMillis = 375
            0.0f at 0 with LinearOutSlowInEasing // for 0-15 ms
            0.2f at 15 with FastOutLinearInEasing // for 15-75 ms
            0.4f at 75 // ms
            0.4f at 225 // ms
        }
    )

    AnimatedVisibility(
        visible = openDialog.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {},
                        onTap = {
                            openDialog.value = false
                        })
                }
                .background(color = Color(0x66000000))
        ) {
            AnimatedVisibility(
                visible = openDialog.value,
                enter = slideInVertically { 0 },
                exit = slideOutVertically { 500 },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
            {
                Surface(
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                    // .align(Alignment.BottomCenter)
                    ,
                    elevation = 10.dp
                ) {
                    AddBirthDayDialogContent(list, {})
                }
            }

        }
    }
}
