package com.ycs.servicetest

import android.util.Log
import com.tencent.mmkv.MMKV
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.IOException


/**
 * Created on 2024/04/08.
 * @author carsonyang
 */
class ParseLinkThread : Thread() {
    val url = "https://xunlangbot.com/download"
    override fun run() {
        super.run()
        try {
            val document = Jsoup.connect(url).get()
            Log.d("yyy", "document:\n$document")
            val elements: Elements = document.getElementsByTag("main")
            Log.e("yyy", "elements:$elements")
            val csrfValue = elements.attr("data-csrf-value")
            Log.e("yyy", "csrfValue:$csrfValue")
            val mmkv = MMKV.mmkvWithID("csrfValue")
            mmkv.encode("csrfValue", csrfValue)
          //  WebUtil.getVideoFromWeb("", ApplicationExt.getContext())

//            val elScriptList =
//                elements[1].data().toString().split("var".toRegex()).dropLastWhile { it.isEmpty() }
//                    .toTypedArray()
//            if (elements.size == 0) {
//                // Toast.makeText(this@WebService, "糟糕，找不到视频！", Toast.LENGTH_SHORT)
//                //  stopSelf()
//                return
//            }
//            Log.e("yyy", "elScriptList.length:" + elScriptList.size + "")
//
//            var script = elScriptList[2]
//            if (script.contains("urls")) {
//                script = script.substring(script.indexOf("urls"), script.length)
//                script = script.substring(0, script.indexOf("}"))
//                val urls = script.split("\"".toRegex()).dropLastWhile { it.isEmpty() }
//                    .toTypedArray()
//                for (url in urls) {
//                    if (url.contains("http")) {
//                        //srcList.add(url)
//                    }
//                }
//                // Log.e("yyy", "长度" + srcList.size())
//                //Toast.makeText(WebService.this, "为您找到"+srcList.size()+"个视频，自动为您下载第一个视频", Toast.LENGTH_SHORT).show();
//
//
//            }
        } catch (e: IOException) {

            Log.e("yyy", "出错" + e.message)
        }


    }

}