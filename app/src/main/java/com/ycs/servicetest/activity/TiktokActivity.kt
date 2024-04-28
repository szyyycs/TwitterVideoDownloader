package com.ycs.servicetest.activity


import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.ycs.servicetest.R
import com.ycs.servicetest.list.RecyclerItemNormalHolder
import com.ycs.servicetest.list.ViewPagerAdapter
import com.ycs.servicetest.model.VideoModel
import com.ycs.servicetest.utils.showToast
import com.ycs.servicetest.view.CustomIosAlertDialog
import kotlinx.android.synthetic.main.tiktok_activity.view_pager2
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.min


class TiktokActivity : AppCompatActivity() {

    private var dataList: MutableList<VideoModel> = mutableListOf()
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private var deleteList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tiktok_activity)
        supportActionBar!!.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dataList = (intent.getSerializableExtra("list") as ArrayList<VideoModel>).toMutableList()
        viewPagerAdapter = ViewPagerAdapter(this, dataList)

        view_pager2.orientation = ViewPager2.ORIENTATION_VERTICAL
        view_pager2.adapter = viewPagerAdapter

        view_pager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                //大于0说明有播放
                val playPosition = GSYVideoManager.instance().playPosition
                if (playPosition >= 0) {
                    //对应的播放列表TAG
                    if (GSYVideoManager.instance().playTag == RecyclerItemNormalHolder.TAG && position != playPosition) {
                        GSYVideoManager.releaseAllVideos()
                        playPosition(position)
                    }
                }
            }

        })

        view_pager2.post {
            playPosition(0)
        }
    }

    override fun onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume(false)
    }

    override fun onDestroy() {
        GSYVideoManager.releaseAllVideos()
        deleteList.forEach {
            val f = File(it)
            if (f.exists()) {
                f.delete()
            }
        }
        super.onDestroy()
    }

    private fun playPosition(position: Int) {
        var viewHolder =
            (view_pager2.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(position)
        if (viewHolder != null) {
            setupPlayer(viewHolder, position)
        } else {
            MainScope().launch {
                delay(200)
                viewHolder =
                    (view_pager2.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(
                        position
                    )
                viewHolder?.let {
                    setupPlayer(it, position)
                }
            }
        }
    }

    private fun setupPlayer(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val recyclerItemNormalHolder = viewHolder as RecyclerItemNormalHolder
        recyclerItemNormalHolder.player.setUp(dataList[position].url, false, "")
        recyclerItemNormalHolder.player.setDeleteFileCallBack {
            CustomIosAlertDialog(this@TiktokActivity)
                .builder()
                .setTitle("提示")
                .setMsg("确认删除该视频吗？")
                .setNegativeButton("取消") { }
                .setPositiveButton("确认") {
                    deleteList.add(dataList[position].url)
                    showToast("删除成功！")
                    view_pager2.currentItem = min(dataList.size - 1, position + 1)
                    MainScope().launch {
                        delay(200)
                        playPosition(min(dataList.size - 1, position + 1))
                    }
                }
                .show()
        }
        recyclerItemNormalHolder.player.startPlayLogic()
    }
}