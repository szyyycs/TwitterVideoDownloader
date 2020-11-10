package com.ycs.servicetest;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;

public class DialogReceiver extends BroadcastReceiver {

    private Context context;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                Toast.makeText(context, "嘻嘻开始下载", Toast.LENGTH_SHORT).show();
            }
        }
    };;

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context=context;
        collapseStatusBar(context);
        new IosAlertDialog(context).builder()
        .setTitle("提示")
                .setEditText("请输入下载链接")
                .setCancelable(false)
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setPositiveButton("开始下载", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handler.sendEmptyMessage(1);
                    }
                }).getWindow()
                .show();

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