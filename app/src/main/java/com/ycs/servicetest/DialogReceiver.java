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

import static com.ycs.servicetest.WebUtil.downloadId;
import static com.ycs.servicetest.WebUtil.isHttpUrl;


public class DialogReceiver extends BroadcastReceiver {

     private Context context;
     private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                if(Build.VERSION.SDK_INT < 29) {
                    //Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    MainService.updateNotification(context,msg.obj.toString());
                }else{
                    //Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    MainService.updateNotification(context,msg.obj.toString());
                }
//                Intent intent=new Intent(context,TestActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//                intent.putExtra("url",msg.obj.toString());
//                context.startActivity(intent);
//
                Intent i=new Intent(context,WebService.class);
                i.putExtra("url",msg.obj.toString());
                context.startService(i);
            }
        }
    };;

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context=context;
        if(PRDownloader.getStatus(downloadId)== Status.RUNNING){
            PRDownloader.pause(downloadId);
            Toast.makeText(context, "下载已暂停", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT < 29) {
                MainService.updateTitle("下载已暂停");
            }
        }else if(PRDownloader.getStatus(downloadId)== Status.PAUSED){
            PRDownloader.resume(downloadId);
            Toast.makeText(context, "下载已重新开始", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT < 29) {
                MainService.updateTitle(context.getResources().getString(R.string.app_name));
            }
        }else{
            collapseStatusBar(context);
            final IosAlertDialog dialog=new IosAlertDialog(context).builder();
            dialog.setTitle("提示")
                    .setEditText("请输入下载链接")
                    .setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .setPositiveButton("开始下载", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isHttpUrl(dialog.getEditText())){
                                Message ms=new Message();
                                ms.what=1;
                                ms.obj=dialog.getEditText();
                                handler.sendMessage(ms);
                                Log.d("yangchaosheng", dialog.getEditText());
                            }else if(WebUtil.isNetworkConnected(context)){
                                Toast.makeText(context, "网络未打开", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(context, "您粘贴的不是网址噢", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setWindow()
                    .setEnter()
                    .show();

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