package com.ycs.servicetest;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class WebService extends Service  {
    private String url;
    private Object object = new Object();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                synchronized(object){
                    object.notifyAll();
                }
                stopSelf();
            }else if(msg.what==2){
                String src=msg.obj.toString();
                WebUtil.download(handler,src,
                        Environment.getExternalStorageDirectory() +"/savedPic/",
                        WebUtil.genearteFileName(),WebService.this);
            }
        }
    };

    private ArrayList<String> srcList=new ArrayList<>();
    public WebService() {

    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url=intent.getStringExtra("url");
        if(!url.contains("http")){
            url="https://"+url;
        }
        if(!url.contains("weibo")){
            Toast.makeText(this, "粘贴的不是微博链接噢！", Toast.LENGTH_SHORT).show();
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Document document= null;
                Document do1= null;
                try {
                    //document = Jsoup.connect("https://www.amazon.cn/b?node=1852543071").get();
                    //Elements elements = document.getElementsByClass("a-carousel-card");

                    do1=Jsoup.connect(url).get();
                    Elements elements=do1.getElementsByTag("script");
                    Log.e("yyy","elements.size:"+elements.size()+"");
                    String[] elScriptList = elements.get(1).data().toString().split("var");

                    if(elements.size()==0){
                        Toast.makeText(WebService.this,"糟糕，找不到视频！",Toast.LENGTH_SHORT);
                        stopSelf();
                        return;
                    }
                    Log.e("yyy","elScriptList.length:"+elScriptList.length+"");
                    //Log.e("yyy","-------------------------element.toString():【"+elScriptList[2]+"】------------------------");
                    String script=elScriptList[2];
                    if(script.contains("urls")){
                        script=script.substring(script.indexOf("urls"),script.length());
                        script=script.substring(0,script.indexOf("}"));

                        String[] urls=script.split("\"");
                        for(String url:urls){
                            if(url.contains("http")){
                                srcList.add(url);
                            }
                        }
                        Log.e("yyy","长度"+srcList.size());
                        //Toast.makeText(WebService.this, "为您找到"+srcList.size()+"个视频，自动为您下载第一个视频", Toast.LENGTH_SHORT).show();
                        //for(String src:srcList) {
                            Log.e("","");
                            synchronized (object) {
                                Message msg = new Message();
                                msg.what = 2;
                                msg.obj = srcList.get(0);
                                handler.sendMessage(msg);
                                try {
                                    object.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                       // }
                    }else{

                    }

                } catch (IOException e) {
                    Toast.makeText(WebService.this, "出错", Toast.LENGTH_SHORT).show();
                    Log.e("yyy","出错"+e.getMessage());
                }

            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }


}
