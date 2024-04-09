package com.ycs.servicetest

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import cn.bmob.v3.Bmob
import com.downloader.PRDownloader
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mmkv.MMKV

import com.ycs.servicetest.utils.WebUtil

/**
 * Created on 2024/04/09.
 * @author carsonyang
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        mAppContext = applicationContext
        //CrashReport.initCrashReport(getApplicationContext(), "b0a053b5dd", true);
        PRDownloader.initialize(mAppContext)
        WebUtil.init(mAppContext)
        Bugly.init(mAppContext, "b0a053b5dd", false)
        MMKV.initialize(this)
        Bmob.initialize(this, "2d24c857824e0609dd2e185bf5378acc")
        Beta.upgradeDialogLayoutId = R.layout.layout_upgrade
    }

    override fun attachBaseContext(base: Context?): Unit {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    companion object {
        private var mAppContext: Context? = null
        fun getAppContext(): Context? {
            return mAppContext
        }
    }

}