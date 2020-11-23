package com.ycs.servicetest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        final Handler h=new Handler();
        getSupportActionBar().hide();
        final TextView textView=findViewById(R.id.tt);
        String url=getIntent().getStringExtra("url");
        if(!url.contains("http")){
            url="https://"+url;
        }
        final String finalUrl = url;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                final StringBuilder str=new StringBuilder();
                Document document= null;
                Document do1= null;
                try {
                    do1= Jsoup.connect(finalUrl).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //str.append(do1.data());
               // Log.e("yyy","————————————————————————————全部数据【"+do1.data()+"】———————————————————————————————————————");
                Elements elements=do1.getElementsByTag("script");
                Log.e("yyy","elements.size:"+elements.size());
               // Log.e("yyy",elements.size()+"");
//                for(Element e:elements){
////                    str.append("第多少条数据:"+e.data());
////                    Log.e("yyy","————————————————————————————一条数据【"+e.data()+"】———————————————————————————————————————");
////                }
              //  String[] elScriptList = elements.get(0).data().toString().split("var");
//                if(elements.size()==0){
//                    Toast.makeText(TestActivity.this,"糟糕，找不到视频！",Toast.LENGTH_SHORT);
//                    return;
//                }
//                for(String s:elScriptList){
                    str.append("第多少条数据:"+elements.get(0)+"\n\n\n\n");
                    Log.e("yy","第多少条数据:"+elements.get(0)+"\n\n\n");
//                }

                //Log.e("yyy","elScriptList.length:"+elScriptList.length+"");
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(str);
                    }
                });
            }
        }).start();
    }
}