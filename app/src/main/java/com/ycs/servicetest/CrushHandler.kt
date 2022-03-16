package com.ycs.servicetest

import android.content.Context
import android.util.Log
import com.ycs.servicetest.utils.LogUtil

class CrashHandler public constructor(): Thread.UncaughtExceptionHandler {
    private var context: Context? = null
    fun init(context: Context?) {
        this.context = context
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        LogUtil.writeLog()
        Log.d(MainActivity.TAG, "开始记录")
    }
    companion object {
        val instance: CrashHandler by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CrashHandler() }
    }
}
