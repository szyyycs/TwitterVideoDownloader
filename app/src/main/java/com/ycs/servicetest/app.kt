package com.ycs.servicetest

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex


open class App : Application() {

//    companion object {
//        private lateinit var app: App
//        @JvmStatic
//        fun getInstance(): App {
//            return app
//        }
//    }
override fun attachBaseContext(base: Context?): Unit {
    super.attachBaseContext(base)
    MultiDex.install(base)
}
    override fun onCreate() {
        super.onCreate()
//        app = this

//        CrashReport.initCrashReport(applicationContext, "b0a053b5dd", false)
    }
}