package com.ycs.servicetest


import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_pubu.*
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*


class PubuActivity : AppCompatActivity() {

    companion object{
        private const val TAG="yyy"
        private val url = Environment.getExternalStorageDirectory().toString()+"/DCIM/Camera/"
    }
    private val imageModels: MutableList<ImageModel> = mutableListOf()
    private var adapter:PubuAdapter= PubuAdapter(imageModels)
    private var mHandler=Handler{
        when(it.what){
            1 -> {
                Log.d(TAG, "更新${imageModels} ")
                adapter.notifyDataSetChanged()
            }
            2->{
                adapter.notifyDataSetChanged()
            }
        }
        true
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pubu)
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        rvv.layoutManager = staggeredGridLayoutManager
        val dividerItemDecoration = SpaceItemDecoration(16)
        rvv.addItemDecoration(dividerItemDecoration)
        rvv.adapter = adapter
        val f = File(url)
        if (!f.exists()) {
            f.mkdirs()
            Log.e(VideoActivity.TAG, "不存在")
        }
        Thread {
            for (s in f.list() ) {
                if (!s.endsWith(".mp4")) {
                    continue
                }
                if (s.substring(s.length - 4, s.length) != ".mp4") {
                    continue
                }
                val uu = url + s
                val file = File(uu)
                val d = BigDecimal(file.length() / (1024 * 1024.0))
                        .setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                val len = d.toString() + "MB"
                var time: String? = null
                if ((s.length == 22 || s.length == 21) && s.startsWith("20")) {
                    time = s.substring(0, 4) + "/" + s.substring(4, 6) + "/" + s.substring(6, 8) + " " + s.substring(8, 10) + ":" + s.substring(10, 12)
                } else {
                        val timeee = file.lastModified()
                        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm")
                        time = formatter.format(timeee)

                }
                // 创建时间
                //String time=s.substring(0,4)+"."+s.substring(4,6)+"."+s.substring(6,8)+" "+s.substring(8,10)+":"+s.substring(10,12);
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
//                val timee = (mediaPlayer!!.duration / 1000).toLong() //获得了视频的时长（以毫秒为单位）
//                mediaPlayer.release()
//                mediaPlayer = null
                var tt: String=""
//                tt = if (timee / 60 != 0L) {
//                    if (timee % 60 < 10) {
//                        (timee / 60).toString() + ":0" + timee % 60
//                    } else {
//                        (timee / 60).toString() + ":" + timee % 60
//                    }
//                } else {
//                    if (timee % 60 < 10) {
//                        "00:0" + timee % 60
//                    } else {
//                        "00:" + timee % 60
//                    }
//                }
                var i=ImageModel(getRandomDouble(), s + time + len + tt, uu)
                imageModels.add(i)

//                mHandler.sendEmptyMessage(1)
            }
            mHandler.sendEmptyMessage(2)
        }.start()

    }
    fun getRandomDouble(): Double {
        val random = Random()
        var nextDouble = random.nextDouble()
        if (nextDouble < 0.1) {
            nextDouble = nextDouble + 0.55
        }
        return nextDouble
    }
}