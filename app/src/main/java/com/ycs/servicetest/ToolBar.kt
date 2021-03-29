package com.ycs.servicetest

import android.view.View

object ToolBar {

        fun View.slideExit() {
            if (translationY == 0f) animate().translationY(-height.toFloat())
        }

        fun View.slideEnter() {
            if (translationY < 0f) animate().translationY(0f)
        }



}