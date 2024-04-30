package com.ycs.servicetest.type

import androidx.compose.runtime.MutableState

/**
 * Created on 2024/04/29.
 * @author carsonyang
 */
sealed class SettingType {
    data class FingerType(val state: MutableState<Boolean>) : SettingType()
    data class ShuffleVideosType(val state: MutableState<Boolean>) : SettingType()
    data class XhsPlayModeType(val state: MutableState<Boolean>) : SettingType()
}