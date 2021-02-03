package com.ycs.servicetest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class DownLoadWindowService extends Service {
    private RelativeLayout view;
    private RelativeLayout view2;
    private ImageView civ;
    private TextView y;
    private int Y=0;
    private Boolean flag=true;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showFloatingWindow();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            // 获取WindowManager服务
            windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
            // 新建悬浮窗控件
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            view = (RelativeLayout) inflater.inflate(R.layout.float_download, null);
            civ=view.findViewById(R.id.civ);
            view.setOnTouchListener(new FloatingOnTouchListener());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendBroadcast(new Intent(DownLoadWindowService.this,DialogReceiver.class));
                }
            });
            // 设置LayoutParam
            layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.format = PixelFormat.RGBA_8888;
//            layoutParams.width = 300;
//            layoutParams.height = 200;
            //layoutParams.gravity = Gravity.RIGHT;
            layoutParams.x = windowManager.getDefaultDisplay().getWidth();
            layoutParams.y = -300;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            windowManager.addView(view, layoutParams);

        }
    }
    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;
        private int yy;
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    yy= (int) event.getRawY();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX =(int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - windowManager.getDefaultDisplay().getWidth()/2;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = movedX;
                    layoutParams.y = layoutParams.y + movedY;

                    // 更新悬浮窗控件布局
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    int Y = (int) event.getRawY();
                    int mY = Y - y;
                    y = Y;
                    int X = (int) event.getRawX();
                    int mX = X - x;
                    x = X;
                    if(layoutParams.x + mX>0){
                        layoutParams.x = windowManager.getDefaultDisplay().getWidth();
                    }else{
                        layoutParams.x=-windowManager.getDefaultDisplay().getWidth();
                    }

                    layoutParams.y = layoutParams.y + mY;
                    // 更新悬浮窗控件布局
                    windowManager.updateViewLayout(view, layoutParams);
                    break;

                default:
                    break;
            }
//            if(yy!=layoutParams.y){
//                //Toast.makeText(DownLoadWindowService.this, "变", Toast.LENGTH_SHORT).show();
//                return true;
//            }else {
                //Toast.makeText(DownLoadWindowService.this, "没变", Toast.LENGTH_SHORT).show();
                return false;
            //}

        }
    }
}
