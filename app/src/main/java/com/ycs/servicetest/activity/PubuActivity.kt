package com.ycs.servicetest.activity


import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.tencent.mmkv.MMKV
import com.ycs.servicetest.R
import com.ycs.servicetest.common.Config
import com.ycs.servicetest.common.Constant.INTENT_LIST
import com.ycs.servicetest.common.Constant.INTENT_POSITION
import com.ycs.servicetest.common.KVKey
import com.ycs.servicetest.list.PubuAdapter
import com.ycs.servicetest.list.PubuAdapter.OnItemClickListener
import com.ycs.servicetest.model.ImageModel
import com.ycs.servicetest.model.VideoModel
import com.ycs.servicetest.utils.KVUtil
import com.ycs.servicetest.utils.showToast
import com.ycs.servicetest.view.CustomIosAlertDialog
import com.ycs.servicetest.view.CustomVideoPlayer
import com.ycs.servicetest.view.SpaceItemDecoration
import kotlinx.android.synthetic.main.activity_pubu.blank_layout
import kotlinx.android.synthetic.main.activity_pubu.rvv
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Random


class PubuActivity : AppCompatActivity() {

    companion object {
        val url by lazy {
            Config.downloadPathUrl
        }
        private const val LOAD_TEXT = 3
        private const val UPDATE_ALL = 2
        private val heightArray = doubleArrayOf(0.45, 0.5, 0.55)
    }

    private lateinit var vibrator: Vibrator
    private var canChange: Boolean = true
    private val detailPlayer: CustomVideoPlayer by lazy {
        findViewById(R.id.detail_player)
    }
    private val xhsPlayModeTypeIsTiktok: Boolean by lazy {
        KVUtil.getBool(
            KVKey.XHS_PLAY_MODE_TYPE,
            Config.DEFAULT_IS_XHS_PLAY_INTO_TIKTOK,
            KVKey.SETTING
        ) ?: Config.DEFAULT_IS_XHS_PLAY_INTO_TIKTOK
    }
    private val kv_text: MMKV by lazy {
        MMKV.mmkvWithID("text")
    }
    private lateinit var orientationUtils: OrientationUtils
    private val imageModels: MutableList<ImageModel> = mutableListOf()
    private var adapter: PubuAdapter = PubuAdapter(imageModels)
    private var isPlay = false
    private var isFullScreen = false
    private var position = 0
    private val videoList by lazy {
        ArrayList<VideoModel>()
    }
    private var mHandler = Handler(Looper.getMainLooper()) {
        when (it.what) {
            1 -> {
                adapter.notifyDataSetChanged()
            }

            UPDATE_ALL -> {
                adapter.notifyDataSetChanged()
                loadText()
            }

            LOAD_TEXT -> {
                adapter.notifyItemChanged(it.obj as Int)
            }
        }
        true
    }

    private fun setStatusBarColor() {
        val window = window
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        getWindow().decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun loadText() {
        Thread {
            run {
                var i = 0
                while (i < imageModels.size) {
                    if (i < imageModels.size) {
                        if (imageModels[i].len.isNullOrBlank()) {
                            imageModels[i].len = loadVideoLen(imageModels[i].url)
                            val msg = Message.obtain()
                            msg.what = LOAD_TEXT
                            msg.obj = i
                            mHandler.sendMessage(msg)
                        }
                    }
                    i++
                }
            }
        }.start()
    }

    private fun hideStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val window = window
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
            getWindow().decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            return
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        val attrs = window.attributes
        attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.attributes = attrs
    }

    private fun showStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val window = window
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
            getWindow().decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            return
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        val attrs = window.attributes
        attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
        window.attributes = attrs
    }

    private fun showVideoView() {
        if (canChange) {
            detailPlayer.slideEnter()
            canChange = false
        }

    }

    private fun View.slideEnter() {
        if (translationY > 0f) animate().translationY(0f)

    }

    private fun View.slideExit() {
        if (translationY == 0f) animate().translationY(resources.getDimension(R.dimen.dp_1000))
    }

    private fun hideVideoView() {
        canChange = true
        detailPlayer.slideExit()
    }

    private fun initStaggeredGridLayout() {
        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        rvv.layoutManager = staggeredGridLayoutManager
        val dividerItemDecoration = SpaceItemDecoration(16)
        rvv.addItemDecoration(dividerItemDecoration)
        rvv.adapter = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                if (xhsPlayModeTypeIsTiktok) {
                    enterTiktokPlayMode(position)
                } else {
                    enterNormalPlayMode(position)
                }

            }

        })
        adapter.setOnLongItemClickListener(object : PubuAdapter.OnLongItemClickListener {
            override fun onLongItemClick(view: View?, postion: Int) {
                CustomIosAlertDialog(this@PubuActivity)
                    .builder()
                    .setTitle("提示")
                    .setMsg("确认删除该视频吗？")
                    .setNegativeButton("取消") { }
                    .setPositiveButton("确认") {
                        val f = File(imageModels[postion].url)
                        if (f.exists()) {
                            f.delete()
                        }
                        showToast("删除成功！")
                        imageModels.removeAt(postion)
                        adapter.update(imageModels)
                        if (imageModels.size == 0) {
                            setBlankUI()
                        }
                    }
                    .show()
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pubu)
        setStatusBarColor()
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (!xhsPlayModeTypeIsTiktok) {
            initPlayer()
        }
        initStaggeredGridLayout()
        loadVideoList()

    }

    private fun loadVideoList() {
        val file = File(url)
        if (!file.exists()) {
            file.mkdirs()
        }
        if (file.list().isNullOrEmpty()) {
            setBlankUI()
            return
        }
        Thread {
            for (s in file.list()!!) {
                if (!s.endsWith(".mp4") || s.startsWith(".")) {
                    continue
                }
                if (s.substring(s.length - 4, s.length) != ".mp4") {
                    continue
                }
                val text: String = kv_text.decodeString(s, "")
                val uu = url + s
                val fileVideo = File(uu)
                val d = BigDecimal(fileVideo.length() / (1024 * 1024.0))
                    .setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                val len = d.toString() + "MB"
                var time = ""
                if ((s.length == 22 || s.length == 21) && s.startsWith("20")) {
                    time = s.substring(0, 4) + "/" + s.substring(4, 6) + "/" + s.substring(
                        6,
                        8
                    ) + " " + s.substring(8, 10) + ":" + s.substring(10, 12)
                } else {
                    val timeee = fileVideo.lastModified()
                    val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA)
                    time = formatter.format(timeee)

                }
                val i = ImageModel(
                    heightArray[getRandomInt()], /*createRepeatedStr("这是一条文案嘻嘻嘻嘻嘻嘻嘻嘻", getRandomInt())*/
                    text,
                    uu,
                    "",
                    time,
                    len
                )
                imageModels.add(i)
            }
            if (imageModels.isEmpty()) {
                setBlankUI()
                return@Thread
            }
            imageModels.shuffle()
            mHandler.sendEmptyMessage(UPDATE_ALL)
        }.start()
    }

    fun setBlankUI() {
        blank_layout.visibility = View.VISIBLE
    }

    fun enterTiktokPlayMode(position: Int) {
        val intent = Intent()
        if (videoList.isEmpty()) {
            for (imageModel in imageModels) {
                val videoModel = VideoModel()
                videoModel.url = imageModel.url
                videoModel.tweet = imageModel.desc
                videoList.add(videoModel)
            }
        }
        Log.d("yang", "position$position")
        intent.putExtra(INTENT_LIST, videoList)
        intent.putExtra(INTENT_POSITION, position)
        intent.setClass(this, TiktokActivity::class.java)
        startActivity(intent)
    }

    fun enterNormalPlayMode(position: Int) {
        isFullScreen = true
        detailPlayer.setUp(imageModels[position].url, true, imageModels[position].desc)
        detailPlayer.visibility = View.VISIBLE
        showVideoView()
        detailPlayer.startPlay()
        hideStatusBar()
        detailPlayer.nextVideo.setOnClickListener(View.OnClickListener {
            if (imageModels.size <= this.position + 1) {
                this.position = (this.position + 1) % imageModels.size
                isPlay = true
                detailPlayer.currentPlayer.release()
                detailPlayer.setUp(
                    imageModels[this.position].url,
                    true,
                    imageModels[this.position].url
                )
                detailPlayer.startPlay()
                return@OnClickListener
            }
            this.position++
            isPlay = true
            detailPlayer.currentPlayer.release()
            detailPlayer.setUp(imageModels[this.position].url, true, imageModels[this.position].url)
            detailPlayer.startPlay()
        })
    }

    override fun onBackPressed() {
        if (!xhsPlayModeTypeIsTiktok) {
            orientationUtils.backToProtVideo()
            if (GSYVideoManager.backFromWindowFull(this)) {
                isFullScreen = false
                //"退出全屏"
                return
            }
            if (!canChange) {
                showStatusBar()
                hideVideoView()
                mHandler.postDelayed({
                    if (isPlay) {
                        detailPlayer.currentPlayer.release()
                        isPlay = false
                    }
                }, 300)
                return
            }
        }

        super.onBackPressed()
    }

    private fun initPlayer() {
        detailPlayer.titleTextView.visibility = View.GONE
        detailPlayer.backButton.visibility = View.GONE
        PlayerFactory.setPlayManager(SystemPlayerManager::class.java)
        orientationUtils = OrientationUtils(this, detailPlayer)
        orientationUtils.isEnable = false
        val gsyVideoOption = GSYVideoOptionBuilder()
        gsyVideoOption
            .setIsTouchWiget(true)
            .setRotateViewAuto(true)
            .setLockLand(false)
            .setAutoFullWithSize(false)
            .setShowFullAnimation(false)
            .setNeedLockFull(true)
            .setLooping(true) //.setUrl(url)
            .setOnlyRotateLand(true) //.setRotateWithSystem(true)
            .setCacheWithPlay(true)
            .setVideoTitle("这里是一个竖直方向的视频")
            .setSeekRatio(1f)
            .setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String, vararg objects: Any) {
                    super.onPrepared(url, *objects)
                    orientationUtils.isEnable = detailPlayer.isRotateWithSystem
                    isPlay = true
                }

                override fun onClickStartThumb(url: String, vararg objects: Any) {
                    super.onClickStartThumb(url, *objects)
                    //hideStatusBar();
                }

                override fun onAutoComplete(url: String, vararg objects: Any) {
                    super.onAutoComplete(url, *objects)
                    isPlay = true
                    detailPlayer.restart()
                }

                override fun onPlayError(url: String, vararg objects: Any) {
                    super.onPlayError(url, *objects)
                    isPlay = false
                    Toast.makeText(this@PubuActivity, "播放错误", Toast.LENGTH_SHORT).show()
                }

                override fun onQuitFullscreen(url: String, vararg objects: Any) {
                    super.onQuitFullscreen(url, *objects)
                    orientationUtils.backToProtVideo()
                    isFullScreen = false
                }

                override fun onTouchScreenSeekVolume(url: String, vararg objects: Any) {
                    super.onTouchScreenSeekVolume(url, *objects)
                }
            })
            .setLockClickListener { view, lock ->
                orientationUtils.isEnable = !lock
            }
            .build(detailPlayer)
        detailPlayer.getVibrate(vibrator)

    }

    private fun loadVideoLen(uu: String): String {
        val mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(uu)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            mediaPlayer.prepare()
        } catch (_: IOException) {
        }
        val timee = (mediaPlayer.duration / 1000).toLong() //获得了视频的时长（以毫秒为单位）
        mediaPlayer.release()
        val tt = if (timee / 60 != 0L) {
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

    private fun createRepeatedStr(seed: String, n: Int): String {
        var s = ""
        for (i in 0..n) {
            s += seed
        }
        return s
    }

    private fun getRandomInt(): Int {
        val random = Random()
        val nextDouble = random.nextDouble()
        return (nextDouble * 3).toInt()
    }

    fun getRandomDouble(): Double {

        val random = Random()
        var nextDouble = random.nextDouble()
        if (nextDouble < 0.4) {
            nextDouble = nextDouble / 2 + 0.4
        }
        if (nextDouble > 0.6) {
            nextDouble = nextDouble / 2 + 0.1
        }
        return nextDouble * 2
    }
}