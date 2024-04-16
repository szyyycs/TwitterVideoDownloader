package com.ycs.servicetest.compose

import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ycs.servicetest.R
import kotlinx.coroutines.flow.collectLatest

/**
 * Created on 2024/04/15.
 * @author carsonyang
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeBox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    bottomContent: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    var bottomWidth by remember { mutableFloatStateOf(0f) }
    var bottomHeight by remember { mutableStateOf(0) }
    val minBound = 0f
    val maxBound = -bottomWidth
    var forceAnimationCheck by remember { mutableStateOf(false) }
    val anchoredDraggableState = remember(maxBound) {
        AnchoredDraggableState(
            initialValue = checked,
            animationSpec = TweenSpec(durationMillis = 1000),
            anchors = DraggableAnchors {
                false at minBound
                true at maxBound
            },
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { maxBound }
        )
    }
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val currentChecked by rememberUpdatedState(checked)
    LaunchedEffect(anchoredDraggableState) {
        snapshotFlow { anchoredDraggableState.currentValue }
            .collectLatest { newValue ->
                if (currentChecked != newValue) {
                    currentOnCheckedChange?.invoke(newValue)
                    forceAnimationCheck = !forceAnimationCheck
                }
            }
    }
    LaunchedEffect(checked, forceAnimationCheck) {
        if (checked != anchoredDraggableState.currentValue) {
            anchoredDraggableState.animateTo(checked)
        }
    }

    Box(
        modifier = modifier
            .anchoredDraggable(
                state = anchoredDraggableState,
                orientation = Orientation.Horizontal,
                enabled = onCheckedChange != null
            )
            .clipToBounds()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(with(LocalDensity.current) {
                    bottomHeight.toDp()
                })
                .onSizeChanged {
                    bottomWidth = it.width.toFloat()
                }
        ) {
            bottomContent()
        }
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = anchoredDraggableState.offset
                }
                .onSizeChanged {
                    bottomHeight = it.height
                }
        ) {
            content()
        }
    }
}

@Composable
fun SlideItem(content: @Composable () -> Unit, onItemDelete: () -> Unit) {
    var checked by remember { mutableStateOf(false) }
    SwipeBox(
        checked = checked,
        onCheckedChange = { checked = it },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        bottomContent = {
            Row {
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .fillMaxHeight()
                        .background(colorResource(R.color.colorRed))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onItemDelete() }
                            )
                        }

                ) {
                    Text(
                        text = "删除",
                        modifier = Modifier.align(Alignment.Center),
                        style = TextStyle.Default.copy(
                            color = colorResource(R.color.white),
                            fontSize = 12.sp
                        )
                    )
                }
            }
        },
        content = { content() }
    )

}
