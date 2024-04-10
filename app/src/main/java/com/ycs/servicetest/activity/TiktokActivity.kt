package com.ycs.servicetest.activity

//import androidx.lifecycle.lifecycleScope
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
import kotlinx.android.synthetic.main.tiktok_activity.view_pager2

class TiktokActivity : AppCompatActivity() {

    var dataList: List<VideoModel>? = ArrayList()
    var viewPagerAdapter: ViewPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tiktok_activity)
        supportActionBar!!.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dataList = intent.getSerializableExtra("list") as ArrayList<VideoModel>?
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
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }

    private fun playPosition(position: Int) {
        val viewHolder =
            (view_pager2!!.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(position)
        if (viewHolder != null) {
            val recyclerItemNormalHolder = viewHolder as RecyclerItemNormalHolder
            recyclerItemNormalHolder.player.setUp(dataList!![position].url, false, "")
            recyclerItemNormalHolder.player.startPlayLogic()
        }
    }

}