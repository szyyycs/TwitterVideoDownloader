package com.ycs.updatetest

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bt_update.setOnClickListener {
            Thread {
                val oldApkFile = File(Environment.getExternalStorageDirectory() , "old.apk")
                val newApkFile = File(Environment.getExternalStorageDirectory(), "new.apk")
                val patchFile = File(Environment.getExternalStorageDirectory(), "patch")
                if(!oldApkFile.exists()){
                    ToastUtils.showShort("老文件不存在")
                    return@Thread
                }
                if(!patchFile.exists()){
                    ToastUtils.showShort("patch文件不存在")
                    return@Thread
                }
                if(!newApkFile.exists()){
                    Log.d("yyy", newApkFile.createNewFile().toString())
                }
                try {
                    PatchUtil.patchAPK(oldApkFile.absolutePath, newApkFile.absolutePath, patchFile.absolutePath)
                }catch (e:Exception){
                    e.message?.let { Log.d("yyy", it) }
                }

                //安装APK
                AppUtils.installApp(newApkFile)
            }.start()
        }
    }


}