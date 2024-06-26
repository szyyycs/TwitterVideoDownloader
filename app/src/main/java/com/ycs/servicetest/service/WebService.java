package com.ycs.servicetest.service;

import static com.ycs.servicetest.utils.WebUtil.analyzeList;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ycs.servicetest.utils.WebUtil;

public class WebService extends Service {
    private static final int STOP_SELF = 1;
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == STOP_SELF) {
                stopSelf();
            }
        }
    };


    public WebService() {

    }


    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("url");
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
//                Log.e("yyy", "analyzeList的值：" + analyzeList);
                Toast.makeText(this, "已加入下载列表", Toast.LENGTH_SHORT).show();
            } else {
//                Log.e("yyy", "analyzeList的值：" + analyzeList);
                Toast.makeText(this, "已在下载列表中", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "链接开始解析", Toast.LENGTH_SHORT).show();
            MainService.updateNotification(WebService.this, "链接正在解析中...");
            WebUtil.isAnalyse = true;
            WebUtil.preDownload(url, WebService.this, handler);
        }


        return super.onStartCommand(intent, flags, startId);
    }


}
