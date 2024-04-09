package com.ycs.servicetest.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.FileProvider
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.OnPauseListener
import com.downloader.OnProgressListener
import com.downloader.OnStartOrResumeListener
import com.downloader.PRDownloader
import com.downloader.Progress
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.ycs.servicetest.Constant
import com.ycs.servicetest.DownLoadWindowService
import com.ycs.servicetest.MainApplication
import com.ycs.servicetest.MainService
import com.ycs.servicetest.TwitterText
import com.ycs.servicetest.model.DownloadResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.FormBody
import okhttp3.FormBody.Builder.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Request.Builder.*
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.sql.Timestamp
import java.util.Date
import java.util.regex.Pattern

object WebUtil {
    private var index = 0
    private var downloadId = 0
    const val STOP_SERVICE = 1

    @JvmField
    var isAnalyse = false

    var downloadMap = HashMap<String, String?>()

    @JvmField
    var analyzeList = ArrayList<String>()

    @JvmField
    var isDownloading = false
    var TAG = "yyy"

    @JvmStatic
    fun isHttpUrl(urls: String): Boolean {
        val regex =
            ("((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
                    + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
                    + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
                    + "(?:" + Patterns.DOMAIN_NAME + ")"
                    + "(?:\\:\\d{1,5})?)" // plus option port number
                    + "(\\/(?:(?:[" + Patterns.GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~" // plus option query params
                    + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
                    + "(?:\\b|$)")
        val pat = Pattern.compile(regex.trim { it <= ' ' }) //对比
        val mat = pat.matcher(urls.trim { it <= ' ' })
        return mat.matches()
    }

    @JvmStatic
    fun isNetworkConnected(context: Context?): Boolean {
        if (context != null) {
            val mConnectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mNetworkInfo = mConnectivityManager.activeNetworkInfo
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable
            }
        }
        return false
    }

    @JvmStatic
    fun init(context: Context?) {
        val config = TwitterConfig.Builder(context)
            .twitterAuthConfig(TwitterAuthConfig(Constant.TWITTER_KEY, Constant.TWITTER_SECRET))
            .build()
        Twitter.initialize(config)
    }


    @JvmStatic
    @Synchronized
    fun preDownload(mUrl: String, context: Context, handler: Handler) {
        //getVideoFromTwitterSdk(mUrl, context, handler, null/*, anotherMethods = ::getVideoFromWeb*/)
        getVideoFromWeb(mUrl, context, handler)
    }

    /*
    *
    * 通过官方sdk获取视频地址的接口已失效
    *
    * */
    @Synchronized
    fun getVideoFromTwitterSdk(
        mUrl: String,
        context: Context,
        handler: Handler,
        anotherMethods: ((mUrl: String, context: Context, handler: Handler) -> Unit)?
    ) {
        val id = getTweetId(mUrl)
        val twitterApiClient = TwitterCore.getInstance().apiClient
        val statusesService = twitterApiClient.statusesService
        val tweetCall = statusesService.show(id, null, null, null)
        tweetCall.enqueue(object : Callback<Tweet>() {
            override fun success(result: Result<Tweet>) {
                isAnalyse = false
                Log.d(TAG, "success: " + result.data)
                if (result.data.extendedEntities == null && (result.data.entities.media == null || result.data.entities.media.size == 0)) {
                    showInformationToUser("链接中未找到文件，下载失败")
                } else if (result.data.extendedEntities != null) {
                    Log.e(TAG, result.data.extendedEntities.media[0].type)
                    var text = result.data.text
                    if (text.contains("http")) {
                        text = text.substring(0, text.indexOf("http"))
                    }
                    text = text.replace("\n", "  ")
                    if (result.data.extendedEntities.media[0].type != "video" &&
                        result.data.extendedEntities.media[0].type != "animated_gif"
                    ) {
                        showInformationToUser("链接中未找到视频，下载失败")
                        anotherMethods?.let { it(mUrl, context, handler) }
                    } else {
                        var url: String
                        var i = 0
                        url = result.data.extendedEntities.media[0].videoInfo.variants[i].url
                        Log.e(TAG, "收到链接啦$url")
                        while (!url.contains(".mp4")) {
                            try {
                                if (result.data.extendedEntities.media[0].videoInfo.variants[i] != null) {
                                    url =
                                        result.data.extendedEntities.media[0].videoInfo.variants[i].url
                                    i += 1
                                }
                            } catch (e: IndexOutOfBoundsException) {
                                downloadVideo(url, context, handler, text)
                            }
                        }
                        downloadVideo(url, context, handler, text)
                        if (analyzeList.contains(mUrl)) {
                            analyzeList.remove(mUrl)
                        }
                        if (analyzeList.size == 0) {
                            isAnalyse = false
                        } else {
                            isAnalyse = true
                            preDownload(analyzeList[0], context, handler)
                        }
                    }
                }
            }

            override fun failure(exception: TwitterException) {
                isAnalyse = false
                if (exception.message?.contains("404") == true) {
                    showInformationToUser("链接失效，失败详情：${exception.message}")
                }
                Log.d("yang", "通过TwitterSDK下载失败")
                anotherMethods?.let { it(mUrl, context, handler) }
            }
        })
    }

    /**
     *
     * 通过第三方网站获取视频地址
     *
     * 优点不用接vpn
     *
     */

    @Synchronized
    fun getVideoFromWeb(mUrl: String, context: Context, handler: Handler) {
        CoroutineScope(Dispatchers.IO).launch {
            val tweetId = getTweetId(mUrl)
            val client = OkHttpClient()
            val builder: FormBody.Builder = FormBody.Builder()
            builder.add("csrf_test_name", "c9540f5b9f43743ef26598bf6573a5e7")
            val formBody: FormBody = builder.build()
            val request: Request = Request.Builder()
                .url("https://xunlangbot.com/twitter_download?id=$tweetId")
                .addHeader("Content-Type", "application/x-www-form-urlencoded;   charset=UTF-8")
                .removeHeader("User-Agent")
                .removeHeader("Origin")
                .addHeader(
                    "Origin",
                    "https://xunlangbot.com"
                )
                .removeHeader("Cookie")
                .addHeader(
                    "Cookie",
                    "csrf_cookie_name=c9540f5b9f43743ef26598bf6573a5e7; ci_sessions=np8cuuo59n4i2e3fius0memo72ssmb42; __vtins__JncVBCoBBv7LYcOw=%7B%22sid%22%3A%20%2254bd52ba-8048-5236-b640-e1e8c89078b4%22%2C%20%22vd%22%3A%202%2C%20%22stt%22%3A%20570044%2C%20%22dr%22%3A%20570044%2C%20%22expires%22%3A%201712549852037%2C%20%22ct%22%3A%201712548052037%7D; __51uvsct__JncVBCoBBv7LYcOw=1; __51vcke__JncVBCoBBv7LYcOw=a59cfed0-8b71-5efc-9915-e7c514438142; __51vuft__JncVBCoBBv7LYcOw=1712547481996; _ga_DKWSDJNXM6=GS1.1.1712547482.1.1.1712548052.0.0.0; _ga=GA1.1.638479943.1712547482; __gads=ID=45c89b9463a0d0c7:T=1712547493:RT=1712548054:S=ALNI_MYNk1Htm1pCGPhnH6Lu4DIJtckX-g; __gpi=UID=00000de3e0db5ca8:T=1712547493:RT=1712548054:S=ALNI_MaczxnYoJ0sA6ay4Cik5yeqFkilhw; __eoi=ID=0cc6e296552daee2:T=1712547493:RT=1712548054:S=AA-AfjajFF5cUXQtGoBoA0rohU0m; FCNEC=%5B%5B%22AKsRol9SYJ8BS_ELJWUQ7OH0gxBRVTuBxVs5riVDrGFvYfy_Tsi9cGbKNhyWAfcuTGRCj1nVr4UifUscOZzr3GteBEsJsU9Qz-wJ1WB3hAmwm6LiO2dW5K5m5hbgqSSnhN_gg2ET_j0pNxArUAS_5KDeGeZNaCfmPw%3D%3D%22%5D%5D"
                )
                .addHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36 Edg/123.0.0.0"
                )
                .post(formBody)
                .addHeader("csrf_test_name", "c9540f5b9f43743ef26598bf6573a5e7")
                .build()
            Log.d(TAG, "request.body(): " + Gson().toJson(request))
            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: Call, e: IOException) {
                    isAnalyse = false
                    Log.d(TAG, "onFailure: " + e.message)
                    showInformationToUser("下载失败,失败详情：${e.message}")
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful && response.body != null) {
                        val body = response.body?.string()
                        Log.d(TAG, "onSuccess: $body")
                        val result =
                            Gson().fromJson(body, DownloadResponse::class.java)
                        showInformationToUser("解析成功，共有${result.variants.size}个文件")
                        for (i in result.variants) {
                            Log.e(TAG, "收到链接啦${i.url}")
                            downloadVideo(i.url, context, handler, "")
                        }
                        if (analyzeList.contains(mUrl)) {
                            analyzeList.remove(mUrl)
                        }
                        if (analyzeList.size == 0) {
                            isAnalyse = false
                        } else {
                            isAnalyse = true
                            preDownload(analyzeList[0], context, handler)
                        }
                    } else {
                        Log.d(TAG, "onFail: " + response.body!!.string())
                        showInformationToUser("解析失败，失败详情：" + response.body!!.string())
                        isAnalyse = false
                    }

                }
            })
        }
    }

    fun showInformationToUser(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(
                MainApplication.getAppContext(), message,
                Toast.LENGTH_SHORT
            ).show()
            MainService.updateNotification(MainApplication.getAppContext(), message)
        }
    }

    @JvmStatic
    fun reverse(str: String?): String {
        return StringBuffer(str.toString()).reverse().toString()
    }

    private fun downloadVideo(url: String, context: Context, handler: Handler, text: String?) {
        if (!isDownloading) {
            val filename = generateFileName()
            if (!text.isNullOrBlank()) {
                val kvText = MMKV.mmkvWithID("text")
                kvText.encode(filename, text)
                val twitterText = TwitterText()
                twitterText.filename = filename
                twitterText.text = reverse(text)
                twitterText.save(object : SaveListener<String>() {
                    override fun done(objectId: String, e: BmobException) {
                        Toast.makeText(context, "上传文案数据失败！", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            download(
                handler, url,
                Environment.getExternalStorageDirectory().toString() + "/.savedPic/",
                filename, context
            )
        } else {
            if (!downloadMap.containsKey(url)) {
                downloadMap[url] = text
            }
        }
    }

    private fun getTweetId(s: String): Long? {
        return try {
            val split = s.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val id = split.last().split("\\?".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[0]
            id.toLong()
        } catch (e: Exception) {
            Log.d(TAG, "getTweetId: " + e.localizedMessage)
            null
        }
    }

    @Synchronized
    fun download(handler: Handler, url: String, path: String, filename: String, context: Context) {
        isDownloading = true
        downloadId = PRDownloader.download(url, path, filename)
            .build()
            .setOnStartOrResumeListener(OnStartOrResumeListener { isDownloading = true })
            .setOnPauseListener(OnPauseListener {})
            .setOnProgressListener(object : OnProgressListener {
                private var flag = true
                private var sum = 0.0
                private var percent = 0.0
                private var currentBytes = 0.0
                override fun onProgress(progress: Progress) {
                    if (flag) {
                        flag = false
                        sum = BigDecimal(progress.totalBytes / (1024 * 1024.0))
                            .setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                        val len = sum.toString() + "MB"
                        Toast.makeText(context, filename + "开始下载，共" + len, Toast.LENGTH_SHORT)
                            .show()
                    }
                    if (sum == 0.0) {
                        sum = BigDecimal(progress.totalBytes / (1024 * 1024.0))
                            .setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                    }
                    currentBytes = BigDecimal(progress.currentBytes / (1024 * 1024.0))
                        .setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                    percent = if (sum != 0.0) {
                        BigDecimal(currentBytes / sum)
                            .setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                    } else {
                        0.5
                    }
                    val progressStr = currentBytes.toString() + "MB/" + sum + "MB"
                    MainService.updateProgress(context, (percent * 100).toInt(), progressStr)
                    DownLoadWindowService.updateProgress((percent * 100).toInt())
                }
            })
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    isDownloading = false
                    Toast.makeText(context, filename + "下载成功", Toast.LENGTH_SHORT).show()
                    val uri: Uri?
                    handler.postDelayed(Runnable {
                        MainService.update(filename + "下载完成")
                        DownLoadWindowService.recover()
                    }, 1000)
                    val f = File(File(path), filename)
                    uri = if (Build.VERSION.SDK_INT >= 24) {
                        FileProvider.getUriForFile(context, "com.ycs.servicetest.provider", f)
                    } else {
                        Uri.fromFile(f)
                    }
                    context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                    if (downloadMap.containsKey(url)) {
                        downloadMap.remove(url)
                    }
                    if (downloadMap.size == 0) {
                        if (analyzeList.size == 0) {
                            handler.sendEmptyMessage(STOP_SERVICE)
                            isAnalyse = false
                        } else {
                            isAnalyse = true
                            preDownload(analyzeList[0], context, handler)
                        }
                    } else {
                        for (key in downloadMap.keys) {
                            downloadVideo(key, context, handler, downloadMap[key])
                            break
                        }
                    }
                }

                override fun onError(error: Error) {
                    isDownloading = false
                    Log.e(
                        TAG,
                        "下载失败！错误信息为连接错误：" + error.isConnectionError + "服务错误：" + error.isServerError
                    )
                    showInformationToUser("下载失败！连接错误：${error.isConnectionError};服务错误：${error.isServerError}")
                    DownLoadWindowService.recover()
                    if (downloadMap.containsKey(url)) {
                        downloadMap.remove(url)
                    }
                    if (downloadMap.size == 0) {
                        handler.sendEmptyMessage(STOP_SERVICE)
                    } else {
                        for (key in downloadMap.keys) {
                            downloadVideo(key, context, handler, downloadMap[key])
                            break
                        }
                    }
                }
            })
    }

    private fun generateFileName(): String {
        val t = Timestamp(Date().time)
        var time = t.toString()
        time = time.replace(".", "")
        time = time.replace(" ", "")
        time = time.replace("-", "")
        time = time.replace(":", "")
        val arr = charArrayOf('a', 'b', 'c', 'd', 'e')
        val rand = arr[index % 5]
        index++
        return "$time$rand.mp4"
    }

    fun createVideoThumbnail(filePath: String?): Bitmap? {
        var bitmap: Bitmap? = null
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(filePath)
            bitmap = retriever.frameAtTime
        } catch (ex: IllegalArgumentException) {
            // Assume this is a corrupt video file
        } catch (ex: RuntimeException) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release()
            } catch (ex: RuntimeException) {
                // Ignore failures while cleaning up.
            }
        }
        return bitmap
    }
}