package com.ycs.servicetest

import android.app.Application
import android.media.MediaPlayer
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.CountListener
import cn.bmob.v3.listener.FindListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import com.ycs.servicetest.list.Items
import com.ycs.servicetest.utils.WebUtil
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat
import java.util.*

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
    var index = MutableLiveData<Int>()
    var loadTweetNum = MutableLiveData<Int>()
    var tweetNum = 0
    var indexUploadTweet = MutableLiveData<Int>()
    var tweet: String = ""
    var len: String = ""
    var tweetCountIndex = -1
    var isScaning = MutableLiveData<Boolean>()
    var context: Application = getApplication()

    //    val gs= GlobalScope
    var url = Environment.getExternalStorageDirectory().toString() + "/.savedPic/"
    private var kv: MMKV = MMKV.defaultMMKV()
    private val kv_text by lazy {
        MMKV.mmkvWithID("text")
    }

    init {
        getSPUrl()
        num.value = 0
        loadTweetNum.value = 0
        isNull.value = true
        isScaning.value = false
        itemsList.value = getDataList(url)
    }

    private fun getSPUrl() {
        val spp = context.getSharedPreferences("url", AppCompatActivity.MODE_PRIVATE)
        if (spp.getString("url", "") != "") {
            url = spp.getString("url", "")!!
        }

    }

    private fun getDataList(tag: String): MutableList<Items> {
        var datalist = mutableListOf<Items>()
        val strJson: String? = kv.decodeString(tag, null) ?: return datalist
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
        num.value = 0
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
        viewModelScope.launch(Dispatchers.Default) { //获取远端数据需要耗时，创建一个协程运行在子线程，不会阻塞
            val getList = async {
                //使用 async 执行一个耗时任务，返回一个deferred
                var tempNum = 0
                var newList = mutableListOf<Items>()
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
                    var instant = null
                    var time: String?
                    if ((s.length == 22 || s.length == 21) && s.startsWith("20")) {
                        time = s.substring(0, 4) + "/" + s.substring(4, 6) + "/" + s.substring(6, 8) + " " + s.substring(8, 10) + ":" + s.substring(10, 12)
                    } else {
                        try {
                            var path: Path?
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                path = file.toPath()
                                attr = Files.readAttributes(path, BasicFileAttributes::class.java)
                                instant = attr.creationTime().toInstant() as Nothing?
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
                    num.postValue(++tempNum)
                    newList.add(0, i)
                }
                sort(newList)
                newList
            }
            val response = getList.await()  //等待deferred 的返回
            GlobalScope.launch(Dispatchers.Main) { //启动一个协程，运行在主线程
                Toast.makeText(context, "共找到${num.value}个视频", Toast.LENGTH_SHORT).show()
                isNull.value = false
                itemsList.value = response
                isScaning.value = false
                loadVideoDuration()
            }

        }

    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    private fun loadVideoDuration() {
        viewModelScope.launch(Dispatchers.Default) {
            val updateIndex = async {
                var list = itemsList.value
                if (list != null) {
                    for (i in list.indices) {
                        if (list != null && list.size != 0) {
                            len = loadVideoLen(list[i]?.url)
                            if (list.isEmpty()) {
                                return@async null
                            }
                            list[i].video_len = len
                            index?.postValue(i)
                        } else {
                            return@async null
                        }

                    }
                }
                list
            }
            val result = updateIndex.await()
            viewModelScope.launch(Dispatchers.Main) { //启动一个协程，运行在主线程
                result?.let {
                    itemsList.value = result
                    if (kv_text.count() == 0L || kv_text.decodeInt("len", 0) < 500) {
                        loadTweet()
                    } else {
                        setDataList(url, result as ArrayList<Items>)
                        isNull.postValue(false)
                    }

                }
            }

        }

    }

    @Synchronized
    private fun handleTwitterList(list: List<TwitterText>) {
        kv_text.encode("len", list.size + kv.decodeInt("len", 0))
        for (tt in list.indices) {
            kv_text.encode(list[tt].filename, WebUtil.reverse(list[tt].text))
            tweet = list[tt].text
            indexUploadTweet.setValue(tt)
        }
        loadTweetNum.value = loadTweetNum.value?.plus(1)
    }


    private fun queryFindList(skip: Int) {
        val query = BmobQuery<TwitterText>()
        query.order("createdAt")
                .setLimit(500)
                .setSkip(skip * 500)
                .findObjects(object : FindListener<TwitterText>() {
                    override fun done(list: MutableList<TwitterText>?, e: BmobException?) {
                        if (e == null && list != null) {
                            handleTwitterList(list)
                        }
                    }
                })
    }

    private fun loadTweet() {
        val query = BmobQuery<TwitterText>()
        query.order("createdAt")
                .count(TwitterText::class.java, object : CountListener() {
                    override fun done(p0: Int?, p1: BmobException?) {
                        if (p0 != null) {
                            tweetNum = p0;
                            tweetCountIndex = p0?.div(500) + 1
                            var num = 0
                            while (num < tweetCountIndex) {
                                queryFindList(num)
                                num++
                            }
                        }
                    }
                })
    }

    fun deSort(stus: ArrayList<Items>) {
        Collections.sort(stus, object : Comparator<Items> {
            override fun compare(o1: Items, o2: Items): Int {
                if (o2.time == null || o1.time == null) {
                    return 1
                }
                return o1.time.compareTo(o2.time)
            }
        })

    }

    fun sortByLarge(stus: ArrayList<Items>) {
        stus.sortWith { o1, o2 ->
            if (o2.video_len == null || o1.video_len == null) {
                1
            }
            o2.video_len.compareTo(o1.video_len)
        }
    }

    fun sortByComment(stus: ArrayList<Items>) {
        stus.sortWith { o1, o2 ->
            if (o2.twittertext == null || o1.twittertext == null) {
                1
            } else o2.twittertext.length - o1.twittertext.length
        }
    }

    fun deSortByComment(stus: ArrayList<Items>) {
        stus.sortWith { o1: Items, o2: Items ->
            if (o2.twittertext == null || o1.twittertext == null) {
                1
            }
            o1.twittertext.length - o2.twittertext.length
        }
    }

    fun desortByLarge(stus: ArrayList<Items>) {
        stus.sortWith { o1: Items, o2: Items ->
            if (o2.video_len == null || o1.video_len == null) {
                1
            }
            o1.video_len.compareTo(o2.video_len)
        }
    }

    fun setDataList(tag: String, dataList: ArrayList<Items>) {
        if (null == dataList || dataList.size <= 0) return
        val gson = Gson()
        //转换成json数据，再保存
        val strJson = gson.toJson(dataList)
        kv.encode(tag, strJson)
    }

    fun sort(list: MutableList<Items>) {
        list.sortWith(Comparator { o1: Items, o2: Items ->
            if (o2.time == null || o1.time == null) {
                return@Comparator 1
            }
            o2.time.compareTo(o1.time)
        })
    }

    private fun loadVideoLen(uu: String): String {
        var tt: String
        var mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(uu)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            mediaPlayer.prepare()
        } catch (e: IOException) {
        }
        var timee: Long
        if (mediaPlayer.duration != null) {
            timee = (mediaPlayer.duration / 1000).toLong()
        } else {
            return ""
        }
        //获得了视频的时长（以毫秒为单位）
        mediaPlayer.release()
        tt = if (timee / 60 != 0L) {
            if (timee % 60 < 10) {
                (timee / 60).toString() + ":0" + timee % 60
            } else {
                (timee / 60).toString() + ":" + timee % 60
            }
        } else {
            if (timee % 60 < 10) {
                "00:0" + timee % 60
            } else {
                "00:" + timee % 60
            }
        }
        return tt
    }
}