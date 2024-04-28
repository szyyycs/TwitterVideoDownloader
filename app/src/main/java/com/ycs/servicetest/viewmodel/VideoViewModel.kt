package com.ycs.servicetest.viewmodel

import android.app.Application
import android.media.MediaPlayer
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.CountListener
import cn.bmob.v3.listener.FindListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import com.ycs.servicetest.MainApplication
import com.ycs.servicetest.common.Config
import com.ycs.servicetest.list.ListItems
import com.ycs.servicetest.model.TwitterText
import com.ycs.servicetest.utils.WebUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Collections
import java.util.Locale

/**
 * <pre>
 *     author : yangchaosheng
 *     e-mail : yangchaosheng@qq.com
 *     time   : 2022/05/20
 *     desc   :
 * </pre>
 */
class VideoViewModel(application: Application) : AndroidViewModel(application) {
    val itemsList = MutableLiveData<MutableList<ListItems>>()
    val isNull = MutableLiveData<Boolean>()
    val num = MutableLiveData<Int>()
    val index = MutableLiveData<Int>()
    val loadTweetNum = MutableLiveData<Int>()
    var tweetNum = 0
    val indexUploadTweet = MutableLiveData<Int>()
    var tweet: String = ""
    var len: String = ""
    var tweetCountIndex = -1
    val isScanning = MutableLiveData<Boolean>()
    val path by lazy {
        Config.downloadPathUrl
    }
    private val kv: MMKV by lazy {
        MMKV.defaultMMKV()
    }
    private val kvText by lazy {
        MMKV.mmkvWithID("text")
    }

    init {
        num.value = 0
        loadTweetNum.value = 0
        isNull.value = true
        isScanning.value = false
        itemsList.value = getDataList(path)
    }


    private fun getDataList(url: String): MutableList<ListItems> {
        val strJson: String = kv.decodeString(url, null) ?: return mutableListOf()
        return Gson().fromJson(strJson, object : TypeToken<MutableList<ListItems?>?>() {}.type)
    }

    private fun checkFileIsNull(): Boolean {
        val f = File(path)
        if (!f.exists()) {
            f.mkdirs()
        }
        if (f.list() == null || f.list()!!.isEmpty()) {
            isNull.value = true
            return true
        } else {
            isNull.value = false
        }
        return isNull.value!!

    }

    fun startScan() {
        num.value = 0
        if (isScanning.value == true) {
            Toast.makeText(
                MainApplication.getAppContext(),
                "正在扫描中，请稍后重试...",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (checkFileIsNull()) {
            return
        }
        isScanning.value = true
        scanItemListFromFile()
    }

    private fun scanItemListFromFile() {
        viewModelScope.launch(Dispatchers.Default) { //获取远端数据需要耗时，创建一个协程运行在子线程，不会阻塞
            val getList = async {
                //使用 async 执行一个耗时任务，返回一个deferred
                var tempNum = 0
                val newList = mutableListOf<ListItems>()
                val f = File(path)
                for (s in f.list()!!) {
                    if (!s.endsWith(".mp4") || s.startsWith(".")) {
                        continue
                    }
                    val uu = path + s
                    val text = kvText.decodeString(s, "")
                    val i = ListItems()
                    val file = File(uu)
                    val d = BigDecimal(file.length() / (1024 * 1024.0))
                        .setScale(2, RoundingMode.HALF_UP).toDouble()
                    val fileSize = d.toString() + "MB"
                    var attr: BasicFileAttributes?
                    var instant: Instant? = null
                    var time: String?
                    if ((s.length == 22 || s.length == 21) && s.startsWith("20")) {
                        time = s.substring(0, 4) + "/" + s.substring(4, 6) + "/" + s.substring(
                            6,
                            8
                        ) + " " + s.substring(8, 10) + ":" + s.substring(10, 12)
                    } else {
                        try {
                            var path: Path?
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                path = file.toPath()
                                attr =
                                    withContext(Dispatchers.IO) {
                                        Files.readAttributes(path, BasicFileAttributes::class.java)
                                    }
                                instant = attr.creationTime().toInstant()
                            }
                            time = if (instant != null) {
                                val temp = instant.toString().replace("T", " ").replace("Z", "")
                                    .replace("-", "/")
                                temp.substring(0, temp.length - 3)
                            } else {
                                val timeee = file.lastModified()
                                val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA)
                                formatter.format(timeee)
                            }
                        } catch (e: Exception) {
                            val timeee = file.lastModified()
                            val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA)
                            time = formatter.format(timeee)

                        }
                    }
                    i.size = fileSize
                    i.text = s
                    i.time = time
                    i.url = uu
                    i.twitterText = text
                    num.postValue(++tempNum)
                    newList.add(0, i)
                }
                sort(newList)
                newList
            }
            val response = getList.await()  //等待deferred 的返回
            CoroutineScope(Dispatchers.Main).launch { //启动一个协程，运行在主线程
                Toast.makeText(
                    MainApplication.getAppContext(),
                    "共找到${num.value}个视频",
                    Toast.LENGTH_SHORT
                ).show()
                isNull.value = false
                itemsList.value = response
                isScanning.value = false
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
                val list = itemsList.value
                if (!list.isNullOrEmpty()) {
                    val mediaPlayer = MediaPlayer()
                    synchronized(mediaPlayer) {
                        for (i in list.indices) {
                            len = loadVideoLen(list[i].url ?: "", mediaPlayer)
                            if (list.isEmpty()) {
                                return@async null
                            }
                            list[i].video_len = len
                            index.postValue(i)
                        }
                    }
                    mediaPlayer.release()
                }
                list
            }
            val result = updateIndex.await()
            viewModelScope.launch(Dispatchers.Main) { //启动一个协程，运行在主线程
                result?.let {
                    itemsList.value = result!!
                    if (kvText.count() == 0L || kvText.decodeInt("len", 0) < 500) {
                        loadTweet()
                    } else {
                        setDataList(path, result as ArrayList<ListItems>)
                        isNull.postValue(false)
                    }

                }
            }

        }

    }

    @Synchronized
    private fun handleTwitterList(list: List<TwitterText>) {
        kvText.encode("len", list.size + kv.decodeInt("len", 0))
        for (tt in list.indices) {
            kvText.encode(list[tt].filename, WebUtil.reverse(list[tt].text))
            tweet = list[tt].text
            indexUploadTweet.value = tt
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
                        tweetNum = p0
                        tweetCountIndex = p0.div(500).plus(1)
                        var num = 0
                        while (num < tweetCountIndex) {
                            queryFindList(num)
                            num++
                        }
                    }
                }
            })
    }

    fun deSort(stus: ArrayList<ListItems>) {
        Collections.sort(stus, object : Comparator<ListItems> {
            override fun compare(o1: ListItems, o2: ListItems): Int {
                if (o2.time == null || o1.time == null) {
                    return 1
                }
                return o1.time.compareTo(o2.time)
            }
        })

    }

    fun sortByLarge(stus: ArrayList<ListItems>) {
        stus.sortWith { o1, o2 ->
            if (o2.video_len == null || o1.video_len == null) {
                1
            }
            o2.video_len.compareTo(o1.video_len)
        }
    }

    fun sortByComment(stus: ArrayList<ListItems>) {
        stus.sortWith { o1, o2 ->
            if (o2.twitterText == null || o1.twitterText == null) {
                1
            } else o2.twitterText.length - o1.twitterText.length
        }
    }

    fun deSortByComment(stus: ArrayList<ListItems>) {
        stus.sortWith { o1: ListItems, o2: ListItems ->
            if (o2.twitterText == null || o1.twitterText == null) {
                1
            }
            o1.twitterText.length - o2.twitterText.length
        }
    }

    fun deSortByLarge(stus: ArrayList<ListItems>) {
        stus.sortWith { o1: ListItems, o2: ListItems ->
            if (o2.video_len == null || o1.video_len == null) {
                1
            }
            o1.video_len.compareTo(o2.video_len)
        }
    }

    fun setDataList(tag: String, dataList: ArrayList<ListItems>) {
        if (dataList.size <= 0) return
        val gson = Gson()
        //转换成json数据，再保存
        val strJson = gson.toJson(dataList)
        kv.encode(tag, strJson)
    }

    fun sort(list: MutableList<ListItems>) {
        list.sortWith(Comparator { o1: ListItems, o2: ListItems ->
            if (o2.time == null || o1.time == null) {
                return@Comparator 1
            }
            o2.time.compareTo(o1.time)
        })
    }

    private fun loadVideoLen(url: String, mediaPlayer: MediaPlayer): String {
        val tt: String
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
        } catch (e: IOException) {
            return "00:00"
        }
        try {
            mediaPlayer.prepare()
        } catch (_: IOException) {
            return "00:00"
        }
        val duration: Long = (mediaPlayer.duration / 1000).toLong()
        //获得了视频的时长（以毫秒为单位）
        tt = if (duration / 60 != 0L) {
            if (duration % 60 < 10) {
                (duration / 60).toString() + ":0" + duration % 60
            } else {
                (duration / 60).toString() + ":" + duration % 60
            }
        } else {
            if (duration % 60 < 10) {
                "00:0" + duration % 60
            } else {
                "00:" + duration % 60
            }
        }
        return tt
    }
}