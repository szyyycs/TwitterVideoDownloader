package com.ycs.servicetest.compose

/**
 * Created on 2024/04/16.
 * @author carsonyang
 */
// ColumnPicker.kt
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 条目高度.
 */
private val itemHeight = 38.dp

/**
 * 透明度控制, 每条目距离 差距
 */
private const val itemAlphaGap = 0.4f

/**
 * 条目数量. 这个暂时不能随便改.  目前得同时改逻辑才行;
 */
private const val columnCount = 5

/**
 * 数字滚轮 Picker
 */
@Composable
fun WheelView(
    modifier: Modifier = Modifier,
    label: (Int) -> String = {
        it.toString()
    },
    value: Int,
    onValueChange: (Int) -> Unit,
    range: Iterable<Int>,
    textStyle: TextStyle = LocalTextStyle.current,
) {
    ListItemPicker(
        modifier = modifier,
        label = label,
        value = value,
        onValueChange = onValueChange,
        list = range.toList(),
        textStyle = textStyle
    )
}


@Composable
fun <T> ListItemPicker(
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
    value: T,
    onValueChange: (T) -> Unit,
    list: List<T>,
    textStyle: TextStyle = LocalTextStyle.current,
) {
    val columnHeightPx = with(LocalDensity.current) { itemHeight.toPx() }

    val coroutineScope = rememberCoroutineScope()

    val animatedOffset = remember { Animatable(0f) }
        .apply {
            val index = list.indexOf(value)
            val offsetRange = remember(value, list) {
                -((list.count() - 1) - index) * columnHeightPx to
                        index * columnHeightPx
            }
            updateBounds(offsetRange.first, offsetRange.second)
        }
    val coercedAnimatedOffset = animatedOffset.value % columnHeightPx
    val currentIndex = getItemIndexForOffset(list, value, animatedOffset.value, columnHeightPx)

    Layout(
        modifier = modifier
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { deltaY ->
                    coroutineScope.launch {
                        animatedOffset.snapTo(animatedOffset.value + deltaY)
                    }
                },
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        val endValue = animatedOffset.fling(
                            initialVelocity = velocity,
                            animationSpec = exponentialDecay(frictionMultiplier = 20f),
                            adjustTarget = { target ->
                                val coercedTarget = target % columnHeightPx
                                val coercedAnchors =
                                    listOf(-columnHeightPx, 0f, columnHeightPx)
                                val coercedPoint =
                                    coercedAnchors.minByOrNull { abs(it - coercedTarget) }!!
                                val base = columnHeightPx * (target / columnHeightPx).toInt()
                                coercedPoint + base
                            }
                        ).endState.value

                        val result = list.elementAt(
                            getItemIndexForOffset(list, value, endValue, columnHeightPx)
                        )
                        onValueChange(result)
                        animatedOffset.snapTo(0f)
                    }
                }
            ),
        content = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight * columnCount)
                    .offset { IntOffset(x = 0, y = coercedAnimatedOffset.roundToInt()) }
            ) {
                val baseLabelModifier = Modifier.align(Alignment.Center)
                ProvideTextStyle(textStyle) {
                    for (index in (currentIndex - 3)..(currentIndex + 3)) {
                        if (index >= 0 && index < list.size) {
                            val off = index - currentIndex
                            val absOff = abs(off)
                            val half = columnHeightPx / 2f

                            // coercedAnimatedOffset: 下拉为正, 上推为负;
                            when {
                                off == -3 && coercedAnimatedOffset > half -> {
                                    // 下拉, 超过一半, -3要提前出现;
                                    val alpha =
                                        getAlpha(absOff, -coercedAnimatedOffset, columnHeightPx)
                                    Label(
                                        text = label(list.elementAt(index)),
                                        modifier = baseLabelModifier
                                            .offset(y = itemHeight * off)
                                            .alpha(alpha),
                                        /*fontSize = getTextSize(
                                            absOff,
                                            -coercedAnimatedOffset,
                                            columnHeightPx
                                        ).sp*/
                                    )
                                }

                                off == -2
                                        && ((coercedAnimatedOffset >= 0) || (coercedAnimatedOffset < 0 && coercedAnimatedOffset > -half))
                                -> {
                                    //  下拉 offset >= 0 则必定显示;  上推, 不超过一半. -2则出现
                                    val alpha =
                                        getAlpha(absOff, -coercedAnimatedOffset, columnHeightPx)
                                    Label(
                                        text = label(list.elementAt(index)),
                                        modifier = baseLabelModifier
                                            .offset(y = itemHeight * off)
                                            .alpha(alpha),
                                        /*fontSize = getTextSize(
                                            absOff,
                                            -coercedAnimatedOffset,
                                            columnHeightPx
                                        ).sp*/
                                    )
                                }

                                off == 3 && coercedAnimatedOffset < -half -> {
                                    // 上推, 超过半个条目. 3则出现;
                                    val alpha =
                                        getAlpha(absOff, coercedAnimatedOffset, columnHeightPx)
                                    Label(
                                        text = label(list.elementAt(index)),
                                        modifier = baseLabelModifier
                                            .offset(y = itemHeight * off)
                                            .alpha(alpha),
//                                        fontSize = getTextSize(
//                                            absOff,
//                                            coercedAnimatedOffset,
//                                            columnHeightPx
//                                        ).sp
                                    )
                                }

                                off == 2
                                        && ((coercedAnimatedOffset <= 0) || (coercedAnimatedOffset > 0 && coercedAnimatedOffset < half))
                                -> {
                                    // 上推 offset <=0 则必定显示;  下拉, 不超过半个条目. 2则出现;
                                    val alpha =
                                        getAlpha(absOff, coercedAnimatedOffset, columnHeightPx)
                                    Label(
                                        text = label(list.elementAt(index)),
                                        modifier = baseLabelModifier
                                            .offset(y = itemHeight * off)
                                            .alpha(alpha),
//                                        fontSize = getTextSize(
//                                            absOff,
//                                            coercedAnimatedOffset,
//                                            columnHeightPx
//                                        ).sp
                                    )
                                }

                                absOff == 1 -> { // -1, 1
                                    val alpha =
                                        getAlpha(absOff, coercedAnimatedOffset, columnHeightPx)
                                    Label(
                                        text = label(list.elementAt(index)),
                                        modifier = baseLabelModifier
                                            .offset(y = itemHeight * off)
                                            .alpha(alpha),
//                                        fontSize = getTextSize(
//                                            absOff,
//                                            coercedAnimatedOffset,
//                                            columnHeightPx
//                                        ).sp
                                    )
                                }

                                off == 0 -> {
                                    Label(
                                        text = label(list.elementAt(index)),
                                        modifier = baseLabelModifier,
                                        color = MaterialTheme.colors.primary,
                                    )
                                }
                            } // when
                        }
                    }
                }
            }
        }
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        layout(constraints.maxWidth, (itemHeight * columnCount).toPx().toInt()) {
            var yPosition = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(x = 0, y = yPosition)
                yPosition += placeable.height
            }
        }
    }
}

/**
 * 根据索引, 以及偏移量 计算透明度;
 * 每级阶差 0.3f
 */
private fun getAlpha(offIndex: Int, offset: Float, columnHeight: Float) =
    1 - offIndex * itemAlphaGap - (offset / columnHeight) * itemAlphaGap

private fun getTextSize(offIndex: Int, offset: Float, columnHeight: Float) =
    (1 - offIndex * itemAlphaGap - (offset / columnHeight) * itemAlphaGap) * 20

/**
 * 文字,居中. 高度固定为 itemHeight;
 * @param text 文字
 * @param color 文字颜色
 */
@Composable
private fun Label(
    text: String,
    modifier: Modifier,
    color: Color = Color.DarkGray,
    //fontSize: TextUnit = 20.sp

) {
    Box(
        modifier = modifier
            .height(itemHeight)
    ) {
        Text(
            text = text,
            modifier = Modifier.align(Alignment.Center),
            color = color,
            // fontSize = fontSize
        )
    }
}

private suspend fun Animatable<Float, AnimationVector1D>.fling(
    initialVelocity: Float,
    animationSpec: DecayAnimationSpec<Float>,
    adjustTarget: ((Float) -> Float)?,
    block: (Animatable<Float, AnimationVector1D>.() -> Unit)? = null,
): AnimationResult<Float, AnimationVector1D> {
    val targetValue = animationSpec.calculateTargetValue(value, initialVelocity)
    val adjustedTarget = adjustTarget?.invoke(targetValue)
    return if (adjustedTarget != null) {
        animateTo(
            targetValue = adjustedTarget,
            initialVelocity = initialVelocity,
            block = block
        )
    } else {
        animateDecay(
            initialVelocity = initialVelocity,
            animationSpec = animationSpec,
            block = block,
        )
    }
}

private fun <T> getItemIndexForOffset(
    range: List<T>,
    value: T,
    offset: Float,
    halfNumbersColumnHeightPx: Float
): Int {
    val indexOf = range.indexOf(value) - (offset / halfNumbersColumnHeightPx).toInt()
    return maxOf(0, minOf(indexOf, range.count() - 1))
}
