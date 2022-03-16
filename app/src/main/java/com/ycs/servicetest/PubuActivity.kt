package com.ycs.servicetest


import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
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
import com.ycs.servicetest.list.PubuAdapter
import com.ycs.servicetest.list.PubuAdapter.OnItemClickListener
import com.ycs.servicetest.utils.IosAlertDialog
import kotlinx.android.synthetic.main.activity_pubu.*
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*


class PubuActivity : AppCompatActivity() {

    companion object{
        private const val TAG="yyy"
       //private var url = Environment.getExternalStorageDirectory().toString()+"/DCIM/Camera/"
        private var url = Environment.getExternalStorageDirectory().toString()+"/.savedPic/"
        private val LOADTEXT=3
        private val UPDATEALL=2
        private val heightArray = doubleArrayOf(0.45, 0.5, 0.55)
    }

    private lateinit var vibrator: Vibrator
    private var canChange: Boolean=true
    private lateinit var detailPlayer: MyVideoPlayer
    private lateinit var kv_text: MMKV
    private lateinit var orientationUtils: OrientationUtils
    private val imageModels: MutableList<ImageModel> = mutableListOf()
    private var adapter: PubuAdapter = PubuAdapter(imageModels)
    private var isPlay=false
    private var isFullScreen=false
    private var position=0
    private lateinit var thumb:RelativeLayout
    private var mHandler=Handler{
        when(it.what){
            1 -> {
                adapter.notifyDataSetChanged()
            }
            UPDATEALL -> {
                adapter.notifyDataSetChanged()
                loadText()
            }
            LOADTEXT -> {
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
                        if (imageModels[i].len == null || imageModels[i].len.isEmpty()) {
                            imageModels[i].len = loadVideoLen(imageModels[i].url)
                            val msg = Message()
                            msg.what = LOADTEXT
                            msg.obj = i
                            mHandler.sendMessage(msg)
                        }
                      //  Log.d(TAG, "$i")
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
    private fun showVideoView(){
        if(canChange){
            detailPlayer.slideEnter()
            canChange=false
        }

    }
    private fun View.slideEnter() {
        if (translationY >0f)animate().translationY(0f)

    }
    private fun View.slideExit() {
        if (translationY ==0f) animate().translationY(resources.getDimension(R.dimen.dp_1000))
    }
    private fun hideVideoView(){
        canChange=true
        detailPlayer.slideExit()
    }
    private fun getSp(){
        val spp = getSharedPreferences("url", MODE_PRIVATE)
        if (spp.getString("url", "") != "") {
            if (url !== spp.getString("url", "")) {
                url = spp.getString("url", "")!!
            }
        }
    }
    public fun initStaggeredGridLayout(){
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        rvv.layoutManager = staggeredGridLayoutManager
        val dividerItemDecoration = SpaceItemDecoration(16)
        rvv.addItemDecoration(dividerItemDecoration)
        rvv.adapter = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, postion: Int) {
                isFullScreen = true
                detailPlayer.setUp(imageModels[postion].url, true, imageModels[postion].desc)
                detailPlayer.visibility = View.VISIBLE
                showVideoView()
                // val p = detailPlayer.startWindowFullscreen(this@PubuActivity, false, true) as MyVideoPlayer
                detailPlayer.startPlay()
                hideStatusBar()
                detailPlayer.getNextVideo().setOnClickListener(View.OnClickListener {
                    if (imageModels.size <= position + 1) {
                        position = (position + 1) % imageModels.size
                        isPlay = true
                        detailPlayer.currentPlayer.release()
                        detailPlayer.setUp(imageModels[position].url, true, imageModels[position].url)
                        detailPlayer.startPlay()
                        return@OnClickListener
                    }
                    position++
                    isPlay = true
                    detailPlayer.currentPlayer.release()
                    detailPlayer.setUp(imageModels[position].url, true, imageModels[position].url)
                    detailPlayer.startPlay()
                })
                // detailPlayer.setUp(itemsList.get(postion).getUrl(), true, itemsList.get(postion).getTwittertext())
            }

        })
        adapter.setOnLongItemClickListener(object : PubuAdapter.OnLongItemClickListener {
            override fun onLongItemClick(view: View?, postion: Int) {
                IosAlertDialog(this@PubuActivity).builder()
                        .setTitle("提示")
                        .setMsg("确认删除该视频吗？")
                        .setNegativeButton("取消") { }
                        .setPositiveButton("确认") {
                            val f = File(imageModels[postion].url)
                            if (f.exists()) {
                                f.delete()
                            }
                            Toast.makeText(this@PubuActivity, "删除成功！", Toast.LENGTH_SHORT).show()
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
        initPlayer()
        getSp()
        initStaggeredGridLayout()
        kv_text = MMKV.mmkvWithID("text")
        val f = File(url)
        if (!f.exists()) {
            f.mkdirs()
            Log.e(VideoActivity.TAG, "不存在")
        }
        if (f.list() == null || f.list().isEmpty()) {
            if (f.list() == null) {
            }
            setBlankUI()
            return
        }
        Thread {
            for (s in f.list() ) {
                if (!s.endsWith(".mp4")) {
                    continue
                }
                if (s.substring(s.length - 4, s.length) != ".mp4") {
                    continue
                }

                val text: String = kv_text.decodeString(s, "")
                val uu = url + s
                val file = File(uu)
                val d = BigDecimal(file.length() / (1024 * 1024.0))
                        .setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                val len = d.toString() + "MB"
                var time= ""
                if ((s.length == 22 || s.length == 21) && s.startsWith("20")) {
                    time = s.substring(0, 4) + "/" + s.substring(4, 6) + "/" + s.substring(6, 8) + " " + s.substring(8, 10) + ":" + s.substring(10, 12)
                } else {
                        val timeee = file.lastModified()
                        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm")
                        time = formatter.format(timeee)

                }

                var tt=""
                var i=ImageModel(heightArray[getRandomInt()], /*createRepeatedStr("这是一条文案嘻嘻嘻嘻嘻嘻嘻嘻", getRandomInt())*/text, uu, tt, time, len)
                imageModels.add(i)
            }
            if (imageModels.isEmpty()) {

                setBlankUI()
                return@Thread
            }

            imageModels.shuffle()
            mHandler.sendEmptyMessage(UPDATEALL)
        }.start()
    }
    fun setBlankUI() {
        blank_layout.visibility = View.VISIBLE
       // Log.d(TAG, "eeeeeeeeeeee")
    }
    override fun onBackPressed() {

        if (orientationUtils != null) {
            orientationUtils.backToProtVideo()
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            isFullScreen = false
            Log.e("yyy", "退出全屏")
            return
        }
        if(!canChange){
            showStatusBar()
            hideVideoView()
            mHandler.postDelayed({
                if (isPlay && detailPlayer != null) {
                    detailPlayer.currentPlayer.release()
                    isPlay = false
                }
            }, 300)
            return
        }
        super.onBackPressed()
    }

    private fun initPlayer(){
        detailPlayer = findViewById<MyVideoPlayer>(R.id.detail_player)
        detailPlayer.getTitleTextView().setVisibility(View.GONE)
        detailPlayer.getBackButton().setVisibility(View.GONE)
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
                        orientationUtils.setEnable(detailPlayer.isRotateWithSystem())
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
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo()
                        }
                        isFullScreen = false
                    }

                    override fun onTouchScreenSeekVolume(url: String, vararg objects: Any) {
                        super.onTouchScreenSeekVolume(url, *objects)
                    }
                })
                .setLockClickListener { view, lock ->
                    if (orientationUtils != null) {
                        orientationUtils.isEnable = !lock
                    }
                }
                .build(detailPlayer)
        detailPlayer.fullscreenButton.setOnClickListener{
//            isFullScreen = true
//            val p = detailPlayer.startWindowFullscreen(this, false, false) as MyVideoPlayer

        }
        detailPlayer.getVibrate(vibrator)

    }
    private fun loadVideoLen(uu: String): String {
        var tt = ""
        var mediaPlayer: MediaPlayer? = MediaPlayer()
        try {
            mediaPlayer!!.setDataSource(uu)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            mediaPlayer!!.prepare()
        } catch (e: IOException) {
        }
        val timee = (mediaPlayer!!.duration / 1000).toLong() //获得了视频的时长（以毫秒为单位）
        mediaPlayer.release()
        mediaPlayer = null
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
    private fun createRepeatedStr(seed: String, n: Int): String {
        var s=""
        for(i in 0..n){
            s+=seed
        }
        return s
    }
    fun getRandomInt():Int{
        val random = Random()
        var nextDouble = random.nextDouble()
        return (nextDouble*3).toInt()
    }
    fun getRandomDouble(): Double {

        val random = Random()
        var nextDouble = random.nextDouble()
        if (nextDouble < 0.4) {
            nextDouble = nextDouble/2 + 0.4
        }
        if (nextDouble > 0.6) {
            nextDouble = nextDouble/2 +0.1
        }
        Log.d(TAG, "getRandomDouble: $nextDouble")
        return nextDouble*2
    }
}