package com.ycs.servicetest

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
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.lxj.xpopup.XPopup
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.ycs.servicetest.list.ItemAdapter
import com.ycs.servicetest.list.Items
import com.ycs.servicetest.utils.IosAlertDialog
import kotlinx.android.synthetic.main.activity_show_video.*
import java.io.File
import java.util.*

class ShowVideoActivity : AppCompatActivity() {
    private var isNull: Boolean=false
    private var vibrator: Vibrator? = null
    private var isScaning = false
    private var itemsList = mutableListOf<Items>()
    private var position = 0
    private var HaveList = false

    private var orientationUtils: OrientationUtils? = null
    private var isPlay = false
    lateinit var adapter: ItemAdapter
    private var canChange = false
    private val viewModel by lazy {
        ViewModelProvider((this as ViewModelStoreOwner?)!!).get(VideoViewModel::class.java)
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
        detail_player.getVibrate(vibrator)
        detail_player.titleTextView.visibility = View.GONE
        detail_player.backButton.visibility = View.GONE
        orientationUtils = OrientationUtils(this, detail_player)
        orientationUtils!!.isEnable = false
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
                        orientationUtils!!.isEnable = detail_player.isRotateWithSystem()
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
                        Toast.makeText(this@ShowVideoActivity, "播放错误", Toast.LENGTH_SHORT).show()
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

            val p = detail_player.startWindowFullscreen(this, false, false) as MyVideoPlayer
            p.getNextVideo().setOnClickListener(View.OnClickListener {

                if (viewModel.itemsList.value?.size!! <= position + 1) {
                    position = (position + 1) % viewModel.itemsList.value?.size!!
                    isPlay = true
                    p.currentPlayer.release()
                    p.setUp(itemsList[position].url, true, itemsList[position].twittertext)
                    p.startPlay()
                    return@OnClickListener
                }
                position++
                isPlay = true
                p.currentPlayer.release()
                p.setUp(itemsList[position].url, true, itemsList[position].twittertext)
                p.startPlay()
            })
        })
        detail_player.getNextVideo().setOnClickListener(View.OnClickListener { v: View? ->
            if (itemsList.size <= position + 1) {
                position = (position + 1) % itemsList.size
                isPlay = true
                detail_player.currentPlayer.release()
                detail_player.setUp(itemsList[position].url, true, itemsList[position].twittertext)
                detail_player.startPlay()
                return@OnClickListener
            }
            position++
            isPlay = true
            detail_player.currentPlayer.release()
            detail_player.setUp(itemsList[position].url, true, itemsList[position].twittertext)
            detail_player.startPlay()
        })
    }

    private fun initData() {
        initViewModel()
    }
    private fun initViewModel(){
        viewModel.itemsList.observe(this) {
            if (it == null) {
                return@observe
            }
            if (it.size == 0) {
                viewModel.isNull.value=true
                viewModel.isScaning.value=false
                //return@observe
            }
            itemsList=it
            adapter?.update(it as ArrayList<Items>?)
        }
        viewModel.isNull.observe(this){
            if(it){
                viewModel.isScaning.value=false
                setBlankUI()
            }else{
                showNormal()
            }
        }
        viewModel.isScaning.observe(this){
            if(!it){
                scan.visibility = View.INVISIBLE
                toScan.visibility = View.VISIBLE
                scanNum.visibility = View.INVISIBLE
            }else{
                scan.visibility = View.VISIBLE
                toScan.visibility = View.INVISIBLE
                scanNum.visibility = View.VISIBLE
            }
        }
        viewModel.num.observe(this){
            scanNum.text = it.toString()
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
        supportActionBar!!.hide()
        setStatusBarColor()
        toScan.visibility = View.INVISIBLE
        scan.visibility = View.VISIBLE
        scanNum.text = "0"
        back.setOnClickListener { finish() }
        intoTiktok.setOnClickListener {
            intoTiktok(true)
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
        var layoutManager = CustomLinearLayoutManager(this)
        layoutManager.setScrollEnabled(true)
        recyclerView.layoutManager = layoutManager
        adapter = ItemAdapter(itemsList as ArrayList<Items>?)
        recyclerView.adapter = adapter
        adapter!!.setOnItemClickListener { view, postion ->
            position = postion
            if (!canChange) {
                isPlay = true
                toolBar.visibility = View.GONE
                canChange = true
                detail_player.setUp(itemsList[postion].url, true, itemsList[postion].twittertext)
                changList()
            } else {
                isPlay = true
                detail_player.currentPlayer.release()
                detail_player.setUp(itemsList[postion].url, true, itemsList[postion].twittertext)
                detail_player.startPlay()
            }
        }
        adapter!!.setOnItemLongClickListener { view: View?, postion: Int ->
            IosAlertDialog(this).builder()
                    .setTitle("提示")
                    .setMsg("确认删除" + itemsList[postion].text + "吗？")
                    .setNegativeButton("取消") { }
                    .setPositiveButton("确认") {
                        val f = File(itemsList[postion].url)
                        if (f.exists()) {
                            f.delete()
                        }
                        Toast.makeText(this, "文件" + itemsList[postion].text + "删除成功！", Toast.LENGTH_SHORT).show()
                        itemsList.removeAt(postion)
                        viewModel.itemsList.value=itemsList

                    }
                    .show()
        }
    }
    private fun setBlankUI() {
        blank.visibility = View.VISIBLE
    }
    private fun showNormal() {
        blank.visibility = View.GONE
    }
    private fun hideStatusBar() {
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        val attrs = window.attributes
        attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.attributes = attrs
    }
    private fun changList() {
        val i = resources.getDimensionPixelSize(R.dimen.dp_400)
        val animatorSet = AnimatorSet()
        val animator4 = ObjectAnimator.ofFloat(recyclerView, "translationY", resources.getDimensionPixelSize(R.dimen.dp_400).toFloat())
        animatorSet.play(animator4)
        animatorSet.setDuration(500).start()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                hideStatusBar()
                val params = recyclerView.layoutParams
                val screenHeight = windowManager.defaultDisplay.height // 屏幕高（像素，如：800p）
                params.height = screenHeight - i + resources.getDimensionPixelSize(R.dimen.dp_40)
                recyclerView.layoutParams = params
                detail_player.startPlay()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }
    private fun selectSortType() {
        XPopup.Builder(this)
                .atView(sort) // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(arrayOf("按下载时间排序", "按视频时长排序", "按描述长度排序"),
                        intArrayOf(R.mipmap.downloadtime, R.mipmap.video, R.mipmap.miaoshu)
                ) { position: Int, text: String? ->
//                        if (position == 0) {
//                            isScaning = false
//                            if (i.get(position) % 2 == 0) {
//                                deSort(itemsList)
//                            } else {
//                                sort(itemsList)
//                            }
//                            i.get(position)++
//                            adapter.update(itemsList)
//                            handler.sendEmptyMessage(VideoActivity.AFTER_SORT_SCAN)
//                        } else if (position == 1) {
//                            isScaning = false
//                            if (i.get(position) % 2 == 0) {
//                                desortByLarge(itemsList)
//                            } else {
//                                sortByLarge(itemsList)
//                            }
//                            i.get(position)++
//                            adapter.update(itemsList)
//                            handler.sendEmptyMessage(VideoActivity.AFTER_SORT_SCAN)
//                        } else if (position == 2) {
//                            isScaning = false
//                            if (i.get(position) % 2 == 0) {
//                                deSortByComment(itemsList)
//                            } else {
//                                sortByComment(itemsList)
//                            }
//                            i.get(position)++
//                            adapter.update(itemsList)
//                            handler.sendEmptyMessage(VideoActivity.AFTER_SORT_SCAN)
//                        }
                }
                .show()
    }

    private fun intoTiktok(isOrder: Boolean){
        if (isNull) {
            Toast.makeText(this, "您视频列表为空，请下载视频后再进入抖音模式哦！", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent()
        val vm = ArrayList<VideoModel?>()
        for (ii in itemsList) {
            val vvv = VideoModel()
            vvv.setUrl(ii.url)
            vm.add(vvv)
        }
        if(!isOrder){
            vm.shuffle()
            Toast.makeText(this, "随机模式", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "顺序模式", Toast.LENGTH_SHORT).show()
        }
        intent.putExtra("list", vm)
        intent.putExtra("i", position)
        intent.setClass(this, tiktok::class.java)
        startActivity(intent)
    }
    private fun setStatusBarColor() {
        val window = window
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        getWindow().decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}