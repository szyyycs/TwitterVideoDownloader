package com.ycs.bsdifftest

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.AppUtils
import com.ycs.bsdifftest.Util.PatchUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bt_update.setOnClickListener { v ->
            Thread {
                val oldApkFile =
                    File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "old.apk")
                val newApkFile =
                    File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "new.apk")
                val patchFile =
                    File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "patch")
                PatchUtil.patchAPK(
                    oldApkFile.getAbsolutePath(),
                    newApkFile.getAbsolutePath(),
                    patchFile.getAbsolutePath()
                )
                //安装APK
                AppUtils.installApp(newApkFile)
            }.start()
        }
    }
}