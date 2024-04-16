package com.ycs.servicetest.utils

import android.view.View

object ToolBarExt {
    fun View.slideExit() {
        if (translationY == 0f) animate().translationY(-height.toFloat())
    }

    fun View.slideEnter() {
        if (translationY < 0f) animate().translationY(0f)
    }


}