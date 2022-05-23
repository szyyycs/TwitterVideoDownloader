package com.ycs.AIDLTest

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta


/**
 * <pre>
 *     author : yangchaosheng
 *     e-mail : yangchaosheng@hisense.com
 *     time   : 2022/04/24
 *     desc   :
 * </pre>
 */
class SampleApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        // 调试时，将第三个参数改为true
        Bugly.init(this, "878be7c8a8", true)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base)


        // 安装tinker
        Beta.installTinker()
    }
}