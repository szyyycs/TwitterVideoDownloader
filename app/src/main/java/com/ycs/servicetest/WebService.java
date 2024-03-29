package com.ycs.servicetest;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ycs.servicetest.utils.WebUtil;

import java.util.ArrayList;

import static com.ycs.servicetest.MainActivity.TAG;
import static com.ycs.servicetest.utils.WebUtil.analyzeList;

public class WebService extends Service {
    private String url;
    private Object object = new Object();
    private static final int STOPSELF = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == STOPSELF) {
                stopSelf();
            } else if (msg.what == 2) {

            }
        }
    };

    private ArrayList<String> srcList = new ArrayList<>();

    public WebService() {

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //LoadingUtil.Loading_show(this);
        Log.e(TAG, "服务开启");

        url = intent.getStringExtra("url");
        if (!url.contains("http")) {
            url = "https://" + url;
        }
        if (!url.contains("twitter")) {
            Toast.makeText(this, "粘贴的不是推特链接噢！", Toast.LENGTH_SHORT).show();
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        if (WebUtil.isAnalyse) {
            if (!analyzeList.contains(url)) {
                WebUtil.analyzeList.add(url);
                Log.e("yyy", "analyzeList的值：" + analyzeList.toString());
                Toast.makeText(this, "已加入下载列表", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("yyy", "analyzeList的值：" + analyzeList.toString());
                Toast.makeText(this, "已在下载列表中", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "链接开始解析", Toast.LENGTH_SHORT).show();
            MainService.updateNotification(WebService.this, "链接正在解析中...");
            WebUtil.isAnalyse = true;
            WebUtil.predownload(url, WebService.this, handler);
        }


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                Document document= null;
//                Document do1= null;
//                try {
//                    //document = Jsoup.connect("https://www.amazon.cn/b?node=1852543071").get();
//                    //Elements elements = document.getElementsByClass("a-carousel-card");
//
//                    do1=Jsoup.connect(url).get();
//                    Elements elements=do1.getElementsByTag("script");
//                    Log.e("yyy","elements.size:"+elements.size()+"");
//                    String[] elScriptList = elements.get(1).data().toString().split("var");
//
//                    if(elements.size()==0){
//                        Toast.makeText(WebService.this,"糟糕，找不到视频！",Toast.LENGTH_SHORT);
//                        stopSelf();
//                        return;
//                    }
//                    Log.e("yyy","elScriptList.length:"+elScriptList.length+"");
//                    //Log.e("yyy","-------------------------element.toString():【"+elScriptList[2]+"】------------------------");
//                    String script=elScriptList[2];
//                    if(script.contains("urls")){
//                        script=script.substring(script.indexOf("urls"),script.length());
//                        script=script.substring(0,script.indexOf("}"));
//
//                        String[] urls=script.split("\"");
//                        for(String url:urls){
//                            if(url.contains("http")){
//                                srcList.add(url);
//                            }
//                        }
//                        Log.e("yyy","长度"+srcList.size());
//                        //Toast.makeText(WebService.this, "为您找到"+srcList.size()+"个视频，自动为您下载第一个视频", Toast.LENGTH_SHORT).show();
//                        //for(String src:srcList) {
//                            Log.e("","");
//                            synchronized (object) {
//                                WebUtil.predownload(url,WebService.this,handler);
//                                try {
//                                    object.wait();
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
        // }
//                    }else{
//
//                    }
//
//                } catch (IOException e) {
//                    Toast.makeText(WebService.this, "出错", Toast.LENGTH_SHORT).show();
//                    Log.e("yyy","出错"+e.getMessage());
//                }

//            }
//        }).start();

        return super.onStartCommand(intent, flags, startId);
    }


}
