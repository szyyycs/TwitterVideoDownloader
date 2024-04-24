package com.ycs.servicetest.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ycs.servicetest.R

/**
 * Created on 2024/04/11.
 * @author carsonyang
 */
//@Preview(backgroundColor = 0xfff)
@Composable
fun CustomToolBar(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(Color.White)
    ) {
        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center),
            // fontWeight = FontWeight(800),
            fontSize = 18.sp,
            color = Color(0xFF444444)
        )
        Image(
            painter = painterResource(id = R.drawable.back),
            contentDescription = "back",
            modifier = Modifier
                .align(
                    Alignment.CenterStart
                )
                .size(40.dp)
                .padding(start = 20.dp)
        )


    }

}

@Preview(showBackground = true)
@Composable
fun tt() {
    CustomToolBar("设置")
}