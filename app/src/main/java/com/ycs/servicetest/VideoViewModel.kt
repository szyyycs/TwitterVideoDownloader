package com.ycs.servicetest

import android.app.Application
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import com.ycs.servicetest.list.Items
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat
import java.time.Instant

/**
 * <pre>
 *     author : yangchaosheng
 *     e-mail : yangchaosheng@hisense.com
 *     time   : 2022/05/20
 *     desc   :
 * </pre>
 */
class VideoViewModel(application: Application) : AndroidViewModel(application) {
    var itemsList = MutableLiveData<MutableList<Items>>()
    var isNull = MutableLiveData<Boolean>()
    var num = MutableLiveData<Int>()
    var isScaning = MutableLiveData<Boolean>()
    var context:Application=application
    init {
        num.value=0
        isNull.value=true
        isScaning.value=false
    }
    private val kv by lazy {
        MMKV.defaultMMKV()
    }
    private val kv_text by lazy {
        MMKV.mmkvWithID("text")
    }
    private var url = Environment.getExternalStorageDirectory().toString() + "/.savedPic/"
    init {
        getSPUrl()
        itemsList.value=getDataList(url)
    }
    private fun getSPUrl() {
        val spp = context.getSharedPreferences("url", AppCompatActivity.MODE_PRIVATE)
        if (spp.getString("url", "") != "") {
            if (url !== spp.getString("url", "")) {
                url = spp.getString("url", "")!!
            }
        }
    }
    private fun getDataList(tag: String?): MutableList<Items> {
        var datalist = mutableListOf<Items>()
        val strJson = kv!!.decodeString(tag, null) ?: return datalist
        val gson = Gson()
        datalist = gson.fromJson(strJson, object : TypeToken<MutableList<Items?>?>() {}.type)
        return datalist
    }
    private fun checkFileIsNull(): Boolean {
        val f = File(url)
        if (!f.exists()) {
            f.mkdirs()
        }
        if (f.list() == null || f.list().isEmpty()) {
            isNull.value=true
            return true
        }else{
            isNull.value=false
        }
        return isNull.value!!

    }
    fun startScan(){
        if(isScaning.value==true){
            Toast.makeText(context, "正在扫描中，请稍后重试...", Toast.LENGTH_SHORT).show()
            return
        }
        if(checkFileIsNull()){
            return
        }
        isScaning.value=true
        scanItemListFromFile()
    }

    private fun scanItemListFromFile(){

        GlobalScope.launch { //获取远端数据需要耗时，创建一个协程运行在子线程，不会阻塞
            val getList = async {
                //使用 async 执行一个耗时任务，返回一个deferred
                var newList= mutableListOf<Items>()
                val f = File(url)
                for (s in f.list()) {
                    if (!s.endsWith(".mp4")) {
                        continue
                    }
                    val uu = url + s
                    val text = kv_text.decodeString(s, "")
                    val i = Items()
                    val file = File(uu)
                    val d = BigDecimal(file.length() / (1024 * 1024.0))
                            .setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                    val fileSize = d.toString() + "MB"
                    var attr: BasicFileAttributes?
                    var instant: Instant? = null
                    var time: String?
                    if ((s.length == 22 || s.length == 21) && s.startsWith("20")) {
                        time = s.substring(0, 4) + "/" + s.substring(4, 6) + "/" + s.substring(6, 8) + " " + s.substring(8, 10) + ":" + s.substring(10, 12)
                    } else {
                        try {
                            var path: Path?
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                path = file.toPath()
                                attr = Files.readAttributes(path, BasicFileAttributes::class.java)
                                instant = attr.creationTime().toInstant()
                            }
                            time = if (instant != null) {
                                val temp = instant.toString().replace("T", " ").replace("Z", "").replace("-", "/")
                                temp.substring(0, temp.length - 3)
                            } else {
                                val timeee = file.lastModified()
                                val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm")
                                formatter.format(timeee)
                            }
                        } catch (e: Exception) {
                            val timeee = file.lastModified()
                            val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm")
                            time = formatter.format(timeee)
                            Log.e(VideoActivity.TAG, "modifiedTime: " + time + e.message)
                        }
                    }
                    i.size = fileSize
                    i.text = s
                    i.time = time
                    i.url = uu
                    i.twittertext = text
                    num.value= num.value?.plus(1)
                    newList.add(0, i)
                }
                newList
            }
            val response = getList.await() //等待deferred 的返回
            GlobalScope.launch(Dispatchers.Main) { //启动一个协程，运行在主线程
                itemsList.value=response
            }
        }

    }

}