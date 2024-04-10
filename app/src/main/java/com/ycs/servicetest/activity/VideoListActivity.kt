package com.ycs.servicetest.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lxj.xpopup.XPopup
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.ycs.servicetest.R
import com.ycs.servicetest.common.Constant
import com.ycs.servicetest.common.Constant.TAG
import com.ycs.servicetest.common.CustomIosAlertDialog
import com.ycs.servicetest.common.CustomLinearLayoutManager
import com.ycs.servicetest.common.CustomVideoPlayer
import com.ycs.servicetest.list.ItemAdapter
import com.ycs.servicetest.list.Items
import com.ycs.servicetest.model.VideoModel
import com.ycs.servicetest.viewmodel.VideoViewModel
import kotlinx.android.synthetic.main.activity_show_video.back
import kotlinx.android.synthetic.main.activity_show_video.blank_layout
import kotlinx.android.synthetic.main.activity_show_video.detail_player
import kotlinx.android.synthetic.main.activity_show_video.intoTiktok
import kotlinx.android.synthetic.main.activity_show_video.recyclerView
import kotlinx.android.synthetic.main.activity_show_video.scan
import kotlinx.android.synthetic.main.activity_show_video.scanNum
import kotlinx.android.synthetic.main.activity_show_video.sort
import kotlinx.android.synthetic.main.activity_show_video.toScan
import kotlinx.android.synthetic.main.activity_show_video.toolBar
import kotlinx.coroutines.cancel
import java.io.File

class VideoListActivity : AppCompatActivity() {

    private var vibrator: Vibrator? = null
    private var itemsList = mutableListOf<Items>()
    private var position = 0
    var sortArray = intArrayOf(0, 0, 0)
    private var orientationUtils: OrientationUtils? = null
    private var isPlay = false
    private var isPause = false
    lateinit var adapter: ItemAdapter
    private var canChange = false
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(VideoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_video)
        initSystem()
        initView()
        initPlay()
        initData()

    }

    private fun initPlay() {
        detail_player.getVibrate(vibrator)
        PlayerFactory.setPlayManager(SystemPlayerManager::class.java)
        detail_player.titleTextView.visibility = View.GONE
        detail_player.backButton.visibility = View.GONE
        orientationUtils = OrientationUtils(this, detail_player)
        orientationUtils?.isEnable = false
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
            .setSeekRatio(1f)
            .setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String, vararg objects: Any) {
                    super.onPrepared(url, *objects)
                    orientationUtils!!.isEnable = detail_player.isRotateWithSystem
                    isPlay = true
                }

                override fun onClickStartThumb(url: String, vararg objects: Any) {
                    super.onClickStartThumb(url, *objects)
                    //hideStatusBar();
                }

                override fun onAutoComplete(url: String, vararg objects: Any) {
                    super.onAutoComplete(url, *objects)
                    isPlay = true
                    detail_player.restart()
                }

                override fun onPlayError(url: String, vararg objects: Any) {
                    super.onPlayError(url, *objects)
                    isPlay = false
                    Toast.makeText(this@VideoListActivity, "播放错误", Toast.LENGTH_SHORT).show()
                }

                override fun onQuitFullscreen(url: String, vararg objects: Any) {
                    super.onQuitFullscreen(url, *objects)
                    if (orientationUtils != null) {
                        orientationUtils!!.backToProtVideo()
                    }

                }

                override fun onTouchScreenSeekVolume(url: String, vararg objects: Any) {
                    super.onTouchScreenSeekVolume(url, *objects)
                }
            })
            .setLockClickListener { view, lock ->
                if (orientationUtils != null) {
                    orientationUtils!!.isEnable = !lock
                }
            }
            .build(detail_player)
        detail_player.fullscreenButton.setOnClickListener(View.OnClickListener {

            val p = detail_player.startWindowFullscreen(this, false, false) as CustomVideoPlayer
            p.nextVideo.setOnClickListener {

                if (viewModel.itemsList.value?.size!! <= position + 1) {
                    position = (position + 1) % viewModel.itemsList.value?.size!!
                    isPlay = true
                    p.currentPlayer.release()
                    p.setUp(itemsList[position].url, true, itemsList[position].twitterText)
                    p.startPlay()
                    return@setOnClickListener
                }
                position++
                isPlay = true
                p.currentPlayer.release()
                p.setUp(itemsList[position].url, true, itemsList[position].twitterText)
                p.startPlay()
            }
        })
        detail_player.nextVideo.setOnClickListener {
            if (itemsList.size <= position + 1) {
                position = (position + 1) % itemsList.size
                isPlay = true
                detail_player.currentPlayer.release()
                detail_player.setUp(itemsList[position].url, true, itemsList[position].twitterText)
                detail_player.startPlay()
                return@setOnClickListener
            }
            position++
            isPlay = true
            detail_player.currentPlayer.release()
            detail_player.setUp(itemsList[position].url, true, itemsList[position].twitterText)
            detail_player.startPlay()
        }
    }

    private fun initData() {
        initViewModel()

        viewModel.startScan()
    }

    private fun initViewModel() {
        viewModel.itemsList.observe(this) {
            if (it == null) {
                return@observe
            }
            if (it.size == 0) {
                viewModel.isNull.value = true
                viewModel.isScanning.value = false
                return@observe
            }
            viewModel.isNull.value = false
            itemsList = it
            adapter.update(it as ArrayList<Items>?)
        }
        viewModel.isNull.observe(this) {
            if (it) {
                viewModel.isScanning.value = false
                setBlankUI()
            } else {
                viewModel.isScanning.value = false
                showNormal()
            }
        }
        viewModel.isScanning.observe(this) {
            if (!it) {
                scan.visibility = View.INVISIBLE
                toScan.visibility = View.VISIBLE
                scanNum.visibility = View.INVISIBLE
            } else {
                scan.visibility = View.VISIBLE
                toScan.visibility = View.INVISIBLE
                scanNum.visibility = View.VISIBLE
            }
        }
        viewModel.num.observe(this) {
            scanNum.text = it.toString()
        }
        viewModel.loadTweetNum.observe(this) {
            if (viewModel.loadTweetNum.value == viewModel.tweetCountIndex) {
                Toast.makeText(this, "加载了${viewModel.tweetNum}条文案", Toast.LENGTH_SHORT).show()
                viewModel.indexUploadTweet.value = -1
                viewModel.isNull.value = false
            }
        }
        viewModel.index.observe(this) {
            itemsList[it].video_len = viewModel.len
            adapter.updateOnepic(it)
        }
        viewModel.indexUploadTweet.observe(this) {
            if (itemsList.isNotEmpty()) {
                if (it == -1) {
                    viewModel.setDataList(viewModel.url, itemsList as ArrayList<Items>)
                    return@observe
                }
                itemsList[it].twitterText = viewModel.tweet
                adapter.updateOnepic(it)
            }
        }

    }

    private fun initSystem() {
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        findPermission()
    }

    private fun findPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                //Toast.makeText(this, "已获得所有权限", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "暂未取得读取文件权限，请前往获取", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, Constant.REQUEST_CODE)
            }
        }
    }

    private fun initView() {
        supportActionBar?.hide()
        setStatusBarColor()
        toScan.visibility = View.INVISIBLE
        scan.visibility = View.VISIBLE
        scanNum.text = "0"
        back.setOnClickListener { finish() }
        intoTiktok.setOnClickListener {
            selectPlayType()
        }
        intoTiktok.setOnLongClickListener {
            intoTiktok(false)
            false
        }
        sort.setOnClickListener {
            selectSortType()
        }
        initRecycleView()
        toScan.setOnClickListener {
            viewModel.startScan()
        }
    }

    private fun initRecycleView() {
        val layoutManager = CustomLinearLayoutManager(this)
        layoutManager.setScrollEnabled(true)
        recyclerView.layoutManager = layoutManager
        adapter = ItemAdapter(itemsList as ArrayList<Items>)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { view, postion ->
            position = postion
            if (!canChange) {
                isPlay = true
                toolBar.visibility = View.GONE
                canChange = true
                detail_player.setUp(itemsList[postion].url, true, itemsList[postion].twitterText)
                changList()
            } else {
                isPlay = true
                detail_player.currentPlayer.release()
                detail_player.setUp(itemsList[postion].url, true, itemsList[postion].twitterText)
                detail_player.startPlay()
            }
        }
        adapter.setOnItemLongClickListener { _: View?, postion: Int ->
            CustomIosAlertDialog(this).builder()
                .setTitle("提示")
                .setMsg("确认删除" + itemsList[postion].text + "吗？")
                .setNegativeButton("取消") { }
                .setPositiveButton("确认") {
                    val f = File(itemsList[postion].url)
                    if (f.exists()) {
                        f.delete()
                    }
                    Toast.makeText(
                        this,
                        "文件" + itemsList[postion].text + "删除成功！",
                        Toast.LENGTH_SHORT
                    ).show()
                    itemsList.removeAt(postion)
                    viewModel.itemsList.value = itemsList

                }
                .show()
        }
    }

    private fun setBlankUI() {
        blank_layout.visibility = View.VISIBLE
    }

    private fun showNormal() {
        blank_layout.visibility = View.GONE
    }

    private fun hideStatusBar() {
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        val attrs = window.attributes
        attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.attributes = attrs
    }

    private fun changList() {
        val height = windowManager.defaultDisplay.height * 3 / 4
        val animatorSet = AnimatorSet()
        val animator4 = ObjectAnimator.ofFloat(recyclerView, "translationY", height.toFloat())
        animatorSet.play(animator4)
        animatorSet.setDuration(500).start()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                hideStatusBar()
                val params = recyclerView.layoutParams
                val screenHeight = windowManager.defaultDisplay.height // 屏幕高（像素，如：800p）
                params.height =
                    screenHeight - height + resources.getDimensionPixelSize(R.dimen.dp_40)
                recyclerView.layoutParams = params
                val playerParam = detail_player.layoutParams
                playerParam.height = height
                detail_player.layoutParams = playerParam
                detail_player.startPlay()

            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun selectPlayType() {
        XPopup.Builder(this)
            .atView(intoTiktok) // 依附于所点击的View，内部会自动判断在上方或者下方显示
            .asAttachList(
                arrayOf("小红书模式", "抖音模式"),
                intArrayOf(R.mipmap.pubulist, R.mipmap.tiktok)
            ) { position: Int, text: String? ->
                when (position) {
                    0 -> {
                        if (viewModel.isNull.value == true) {
                            Toast.makeText(
                                this,
                                "您视频列表为空，请下载视频后再进入小红书模式哦！",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@asAttachList
                        }
                        startActivity(Intent(this@VideoListActivity, PubuActivity::class.java))
                        finish()
                    }

                    1 -> {
                        intoTiktok(false)
                    }
                }
            }
            .show()
    }

    private fun selectSortType() {
        XPopup.Builder(this)
            .atView(sort) // 依附于所点击的View，内部会自动判断在上方或者下方显示
            .asAttachList(
                arrayOf("按下载时间排序", "按视频时长排序", "按描述长度排序"),
                intArrayOf(R.mipmap.downloadtime, R.mipmap.video, R.mipmap.miaoshu)
            ) { position: Int, text: String? ->
                if (viewModel.isScanning.value == true) {
                    Toast.makeText(this, "正在扫描中，请稍后再试", Toast.LENGTH_SHORT).show()
                    return@asAttachList
                }
                when (position) {
                    0 -> {

                        if (sortArray[position] % 2 == 0) {
                            viewModel.deSort(itemsList as ArrayList<Items>)
                        } else {
                            viewModel.sort(itemsList)
                        }
                        sortArray[position]++
                        viewModel.itemsList.value = itemsList


                    }

                    1 -> {

                        if (sortArray[position] % 2 == 0) {
                            viewModel.deSortByLarge(itemsList as ArrayList<Items>)
                        } else {
                            viewModel.sortByLarge(itemsList as ArrayList<Items>)
                        }
                        sortArray[position]++
                        viewModel.itemsList.value = itemsList

                    }

                    2 -> {

                        if (sortArray[position] % 2 == 0) {
                            viewModel.deSortByComment(itemsList as ArrayList<Items>)
                        } else {
                            viewModel.sortByComment(itemsList as ArrayList<Items>)
                        }
                        sortArray[position]++
                        viewModel.itemsList.value = itemsList
                    }
                }
            }
            .show()
    }

    private fun intoTiktok(isOrder: Boolean) {
        if (viewModel.isNull.value == true) {
            Toast.makeText(this, "您视频列表为空，请下载视频后再进入抖音模式哦！", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val intent = Intent()
        val vm = ArrayList<VideoModel?>()
        for (ii in itemsList) {
            val vvv = VideoModel()
            vvv.url = ii.url
            vvv.tweet = ii.twitterText
            vm.add(vvv)
        }
        if (!isOrder) {
            vm.shuffle()
            Toast.makeText(this, "随机模式", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "顺序模式", Toast.LENGTH_SHORT).show()
        }
        intent.putExtra("list", vm)
        intent.putExtra("i", position)
        intent.setClass(this, TiktokActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setStatusBarColor() {
        val window = window
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        getWindow().decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    override fun onPause() {
        detail_player.currentPlayer.onVideoPause()
        super.onPause()
        isPause = true
    }

    override fun onResume() {
        detail_player.currentPlayer.onVideoResume(false)
        super.onResume()
        isPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        detail_player.currentPlayer.release()
        itemsList.clear()
        if (orientationUtils != null) orientationUtils!!.releaseListener()
        viewModel.viewModelScope.cancel()
    }


    override fun onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils?.backToProtVideo()
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            Log.e(TAG, "退出全屏")
            return
        }
        if (canChange) {
            if (isPlay) {
                detail_player.currentPlayer.release()
                isPlay = false
            }
            reChangeList()
            canChange = false
            return
        } else {
            super.onBackPressed()
            finish()
        }
        super.onBackPressed()
    }

    private fun showStatusBar() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        val attrs = window.attributes
        attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
        window.attributes = attrs
    }

    private fun reChangeList() {
        showStatusBar()
        val screenHeight = windowManager.defaultDisplay.height // 屏幕高（像素，如：800p）
        val animatorSet = AnimatorSet()
        val params = recyclerView.layoutParams
        params.height = screenHeight
        recyclerView.layoutParams = params
        toolBar.visibility = View.VISIBLE
        val animator4 = ObjectAnimator.ofFloat(recyclerView, "translationY", 0f)
        animatorSet.play(animator4)
        animatorSet.setDuration(500).start()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                detail_player.currentPlayer.onVideoPause()
                isPause = true
                if (isPlay) {
                    isPlay = false
                    detail_player.currentPlayer.release()
                }
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

}