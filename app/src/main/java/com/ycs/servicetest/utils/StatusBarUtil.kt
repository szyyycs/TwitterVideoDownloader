package com.ycs.servicetest.utils

import android.app.Activity
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.ycs.servicetest.R

/**
 * Created on 2024/04/12.
 * @author carsonyang
 */
object StatusBarUtil {
    fun setStatusBarColor(activity: Activity, color: Int = R.color.white, isLight: Boolean) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        activity.window.statusBarColor = ContextCompat.getColor(activity, color)
        if (isLight) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

}