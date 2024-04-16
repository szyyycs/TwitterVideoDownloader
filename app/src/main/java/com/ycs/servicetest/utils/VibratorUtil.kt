package com.ycs.servicetest.utils

import android.os.Build
import android.os.VibrationEffect.createOneShot
import android.os.Vibrator
import androidx.core.content.ContextCompat.getSystemService
import com.ycs.servicetest.MainApplication

/**
 * Created on 2024/04/16.
 * @author carsonyang
 */
object VibratorUtil {
    private val vibrator: Vibrator by lazy {
        getSystemService(MainApplication.getAppContext()!!, Vibrator::class.java) as Vibrator
    }


    fun vibrator(time: Long) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            vibrator.vibrate(createOneShot(time, 100))
        } else {
            vibrator.vibrate(time)
        }

    }
}