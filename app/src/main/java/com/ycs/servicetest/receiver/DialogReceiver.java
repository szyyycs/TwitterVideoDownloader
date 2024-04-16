package com.ycs.servicetest.receiver;

import static com.ycs.servicetest.utils.WebUtil.isHttpUrl;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ycs.servicetest.service.MainService;
import com.ycs.servicetest.service.WebService;
import com.ycs.servicetest.utils.ClipBoardUtil;
import com.ycs.servicetest.view.CustomIosAlertDialog;

import java.lang.reflect.Method;


public class DialogReceiver extends BroadcastReceiver {
    private static final int GOTO_DOWNLOAD = 1;
    private Context context;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == GOTO_DOWNLOAD) {
                MainService.updateNotification(context, "链接正在解析中...");
                Intent i = new Intent(context, WebService.class);
                i.putExtra("url", msg.obj.toString());
                context.startService(i);
            }
        }
    };

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String[] paste = {"请输入下载链接"};
        this.context = context;
        collapseStatusBar(context);
        final CustomIosAlertDialog dialog = new CustomIosAlertDialog(context).builder();
        dialog.setTitle("提示")
                .setEditText(paste[0])
                .setNegativeButton("取消", v -> {

                })

                .setPositiveButton("开始下载", v -> {
                    String text = dialog.getEditText();
                    if (text.isEmpty()) {
                        text = dialog.getHint();
                    }
                    if (isHttpUrl(text)) {
                        Message ms = new Message();
                        ms.what = GOTO_DOWNLOAD;
                        ms.obj = text;
                        handler.sendMessage(ms);
                    } else {
                        Toast.makeText(context, "您粘贴的不是网址噢", Toast.LENGTH_SHORT).show();
                    }

                })
                .setWindow()
                .setEnter()
                .show();
        handler.post(() -> dialog.getWindows().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                paste[0] = new ClipBoardUtil(context).paste();
                if (paste[0] == null || paste[0].isEmpty()) {
                    paste[0] = "请输入下载链接";
                }
                dialog.setEditText(paste[0]);
            }
        }));


    }

    public void collapseStatusBar(Context context) {
        try {
            @SuppressLint("WrongConstant")
            Object statusBarManager = context.getSystemService("statusbar");
            Method collapse;
            collapse = statusBarManager.getClass().getMethod("collapsePanels");
            collapse.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
        }

    }


}