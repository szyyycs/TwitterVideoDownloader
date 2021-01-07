package com.ycs.servicetest;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.downloader.PRDownloader;
import com.downloader.Status;

import java.lang.reflect.Method;

import static com.ycs.servicetest.WebUtil.analyzeList;
import static com.ycs.servicetest.WebUtil.downloadId;
import static com.ycs.servicetest.WebUtil.isAnalyse;
import static com.ycs.servicetest.WebUtil.isHttpUrl;


public class DialogReceiver extends BroadcastReceiver {
     private static final int GOTO_DOWNLOAD=1;
     private Context context;

     private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==GOTO_DOWNLOAD){
                isAnalyse=true;
                MainService.updateNotification(context,"链接正在解析中...");
                Intent i=new Intent(context,WebService.class);
                i.putExtra("url",msg.obj.toString());
                context.startService(i);
            }
        }
    };;

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String[] paste = {"请输入下载链接"};
        this.context=context;
        if(PRDownloader.getStatus(downloadId)== Status.RUNNING||WebUtil.isDownloading){
            Toast.makeText(context, "正在下载中", Toast.LENGTH_SHORT).show();
//            PRDownloader.pause(downloadId);
//            Toast.makeText(context, "下载已暂停", Toast.LENGTH_SHORT).show();
//            if (Build.VERSION.SDK_INT < 29) {
//                MainService.updateTitle("下载已暂停");
//            }
//        }else if(PRDownloader.getStatus(downloadId)== Status.PAUSED){
//            PRDownloader.resume(downloadId);
//            Toast.makeText(context, "下载已继续", Toast.LENGTH_SHORT).show();
//            if (Build.VERSION.SDK_INT < 29) {
//                MainService.updateTitle(context.getResources().getString(R.string.app_name));
//            }
        }else if(isAnalyse){
            Toast.makeText(context, "正在解析链接中，请稍后再粘贴下载", Toast.LENGTH_SHORT).show();
        }else{
            collapseStatusBar(context);
            final IosAlertDialog dialog=new IosAlertDialog(context).builder();
            dialog.setTitle("提示")
                    .setEditText(paste[0])
                    .setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })

                    .setPositiveButton("开始下载", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String text=dialog.getEditText();
                            if(text.isEmpty()){
                                text=dialog.getHint();
                            }
                            if(isHttpUrl(text)){
                                Message ms=new Message();
                                ms.what=GOTO_DOWNLOAD;
                                ms.obj=text;
                                handler.sendMessage(ms);
                            }else {
                                Toast.makeText(context, "您粘贴的不是网址噢", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setWindow()
                    .setEnter()
                    .show();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dialog.getWindows().getDecorView().post(new Runnable() {
                        @Override
                        public void run() {
                            paste[0] =new ClipBoardUtil(context).paste();
                            if(paste[0] ==null|| paste[0].isEmpty()){
                                paste[0] ="请输入下载链接";
                            }
                            dialog.setEditText(paste[0]);
                        }
                    });
                }
            });

        }

    }

    public  void collapseStatusBar(Context context) {
        try {
            @SuppressLint("WrongConstant") Object statusBarManager = context.getSystemService("statusbar");
            Method collapse;
            if (Build.VERSION.SDK_INT <= 16) {
                collapse = statusBarManager.getClass().getMethod("collapse");
            } else {
                collapse = statusBarManager.getClass().getMethod("collapsePanels");
            }
            collapse.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
        }

    }


}