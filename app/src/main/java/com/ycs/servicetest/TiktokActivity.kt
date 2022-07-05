package com.ycs.servicetest

//import androidx.lifecycle.lifecycleScope
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.ycs.servicetest.list.RecyclerItemNormalHolder
import com.ycs.servicetest.list.ViewPagerAdapter
import kotlinx.android.synthetic.main.tiktok_activity.*
import java.util.*

class TiktokActivity : AppCompatActivity() {

    var dataList: List<VideoModel>? = ArrayList()
    var showList: List<VideoModel> = ArrayList()
    var onePageLen = 4
    var subIndex = onePageLen
    var foot: View? = null
    var viewPagerAdapter: ViewPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tiktok_activity)
        supportActionBar!!.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        dataList = intent.getSerializableExtra("list") as ArrayList<VideoModel>?
        viewPagerAdapter = ViewPagerAdapter(this, dataList)
        // view_pager2.setOffscreenPageLimit(1)
        view_pager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL)
        view_pager2.setAdapter(viewPagerAdapter)
        setSupportsChangeAnimations(view_pager2, false)
        view_pager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                //大于0说明有播放
                val playPosition = GSYVideoManager.instance().playPosition
                if (playPosition >= 0) {
                    //对应的播放列表TAG
                    if (GSYVideoManager.instance().playTag == RecyclerItemNormalHolder.TAG && position != playPosition) {
                        // lifecycleScope.launch(Dispatchers.Main) {
                        GSYVideoManager.releaseAllVideos()
                        // view_pager2.postDelayed({
                        playPosition(position)
                        // },200)

                        //   }
                        // view_pager2.postDelayed({  }, 300)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
        view_pager2.post(Runnable {
            playPosition(0)
            Log.d("kadun", "playPosition(0)")
        })
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
        Log.d("yyy", "releaseAll")
        GSYVideoManager.releaseAllVideos()
    }

    private fun setSupportsChangeAnimations(viewPager: ViewPager2?, enable: Boolean) {
        for (i in 0 until viewPager!!.childCount) {
            val view = viewPager.getChildAt(i)
            if (view is RecyclerView) {
                val animator = view.itemAnimator
                if (animator != null) {
                    (animator as SimpleItemAnimator).supportsChangeAnimations = enable
                }
                break
            }
        }
    }

    private fun resolveData() {
        if (viewPagerAdapter != null) viewPagerAdapter!!.notifyDataSetChanged()
    }

    private fun playPosition(position: Int) {
        val viewHolder = (view_pager2!!.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(position)
        if (viewHolder != null) {
            val recyclerItemNormalHolder = viewHolder as RecyclerItemNormalHolder
            recyclerItemNormalHolder.player.setUp(dataList!![position].getUrl(), false, "")
            recyclerItemNormalHolder.player.startPlayLogic()
        }
    }

}