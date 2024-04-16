package com.ycs.servicetest.compose

/**
 * Created on 2024/04/16.
 * @author carsonyang
 */

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ycs.servicetest.MainApplication
import com.ycs.servicetest.R


/**
 * 日期选择器;
 */
@Composable
fun DatePicker(
    modifier: Modifier = Modifier,
    year: Int = 2024,
    month: Int = 1,
    day: Int = 1,
    title: String = "选择日期",
    onDismiss: (selected: Boolean, month: Int, day: Int, name: String) -> Unit
) {
    var selectYear by remember { mutableStateOf(year) }
    var selectMonth by remember { mutableStateOf(month) }
    val selectDay = remember { mutableStateOf(day) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 4.dp),
    ) {
//        TitleBar(
//            title = title,
//            endText = "确定",
//            endClick = {
//                onDismiss(true, selectYear, selectMonth, selectDay.value)
//            },
//        ) { onDismiss(false, 0, 0, 0) }
        Box(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 20.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.close),
                contentDescription = "close",
                modifier = Modifier
                    .padding(end = 10.dp, top = 10.dp)
                    .width(15.dp)
                    .height(15.dp)
                    .align(Alignment.TopEnd)
                    .clickable {
                        onDismiss(false, 0, 0, "")
                    }

            )
        }
        var text by remember { mutableStateOf("") }
        val focusRequest = remember {
            FocusRequester()
        }
        val isError = remember {
            mutableStateOf(false)
        }
        OutlinedTextField(
            value = text,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 30.dp)
                .padding(bottom = 10.dp)
                .focusRequester(focusRequest),
            onValueChange = {
                text = it
                if (text.isNotBlank()) {
                    isError.value = false
                }
            },
            label = { Text("备注名") },
            singleLine = true,
            isError = isError.value
        )
//        LaunchedEffect(Unit) {
//            focusRequest.requestFocus()
//        }
        DateWheel(selectMonth, selectDay) { index, value ->
            when (index) {
//                0 -> selectYear = value
                0 -> selectMonth = value
                1 -> selectDay.value = value
            }
        }


        FilledTonalButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 20.dp),
            colors = ButtonColors(
                containerColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.primary,
                disabledContainerColor = MaterialTheme.colors.primary,
                disabledContentColor = MaterialTheme.colors.primary
            ),
            onClick = {
                if (text.isBlank()) {
                    isError.value = true
                    Toast.makeText(
                        MainApplication.getAppContext(),
                        "备注名必填",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@FilledTonalButton
                }
                onDismiss(true, selectMonth, selectDay.value, text.trim())
            }) {
            Text(text = "确定", color = Color.White)
        }
    }
}

/**
 * 时间选择器 - 睡眠 - (开始-结束时间)
 */
@Composable
private fun DateWheel(
    //year: Int = 2024,
    month: Int,
    day: MutableState<Int>,
    onChange: (index: Int, value: Int) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        Alignment.Center
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
        ) {
            val modifier = Modifier.weight(1f)

            //  年
            /* WheelView(
                 modifier = modifier,
                 value = year,
                 label = { "${it}年" },
                 range = 1920..2060,
                 onValueChange = {
                     onChange(0, it)
                 }
             )*/
            //  月
            WheelView(
                modifier = modifier,
                value = month,
                label = { "$it 月" },
                range = 1..12,
                onValueChange = {
                    onChange(0, it)
                }
            )

            //  日
            val lastDay = getLastDayWithNoYear(month)
            if (day.value > lastDay) day.value = lastDay
            WheelView(
                modifier = modifier,
                value = day.value,
                label = { "$it 日" },
                range = 1..lastDay,
                onValueChange = {
                    onChange(1, it)
                }
            )
        }

        // 中间两道横线
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .align(Alignment.Center)
        ) {
            Divider(Modifier.padding(horizontal = 25.dp))
            Divider(
                Modifier
                    .padding(horizontal = 25.dp)
                    .align(Alignment.BottomStart)
            )
        }
    }
}

/**
 * 根据年月, 获取天数
 */
private fun getLastDay(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        else -> {
            // 百年: 四百年一闰年;  否则: 四年一闰年;
            if (year % 100 == 0) {
                if (year % 400 == 0) {
                    29
                } else {
                    28
                }
            } else {
                if (year % 4 == 0) {
                    29
                } else {
                    28
                }
            }
        }
    }
}

private fun getLastDayWithNoYear(month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        else -> 29
    }
}

private fun formatNumber(number: Int): String {
    return if (number < 10) "0$number" else number.toString()
}

@Preview
@Composable
private fun TimePreview() {
    DatePicker { selected, month, day, date ->
    }
}
