package com.ycs.ctest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import com.blankj.utilcode.util.AppUtils
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
                if(oldApkFile.exists()){
                    Log.d("yyy", "老文件不存在 ")
                }else{
                    Log.d("yyy", "老文件存在 ")
                }
                if(newApkFile.exists()){
                    Log.d("yyy", "新文件文件不存在 ")
                }else{
                    Log.d("yyy", "新文件存在 ")
                }
                if(patchFile.exists()){
                    Log.d("yyy", "patch文件不存在 ")
                }else{
                    Log.d("yyy", "patch文件存在 ")
                }
                PatchUtil.patchAPK(
                        oldApkFile.absolutePath,
                        newApkFile.absolutePath,
                        patchFile.absolutePath
                )
                //安装APK
                AppUtils.installApp(newApkFile)
            }.start()
        }

    }



}