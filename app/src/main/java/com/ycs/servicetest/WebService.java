package com.ycs.servicetest;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
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
        //url=intent.getStringExtra("url");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document document= null;
                try {
                    document = Jsoup.connect("https://www.amazon.cn/b?node=1852543071").get();
                    Elements elements = document.getElementsByClass("a-carousel-card");
                    Log.e("yyy",elements.size()+"");
                    if(elements.size()==0){
                        Toast.makeText(WebService.this,"糟糕，太频繁了！",Toast.LENGTH_SHORT);
                        stopSelf();
                        return;
                    }
                    for (Element li : elements) {
                        Element content =li.select("img").first();
                        String bookImgUrl=content.attr("src");
                        srcList.add(bookImgUrl);
                        //Log.e("yyy", bookTitle);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WebService.this, "开始下载"+srcList.size()+"项", Toast.LENGTH_SHORT).show();
                            Log.e("yyy","开始下载"+srcList.size()+"项");
                        }
                    });
                    for(String src:srcList){
                        synchronized (object){
                            Message msg=new Message();
                            msg.what=2;
                            msg.obj=src;
                            handler.sendMessage(msg);
                            try {
                                object.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

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
