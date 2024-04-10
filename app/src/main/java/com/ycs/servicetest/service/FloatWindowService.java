package com.ycs.servicetest.service;

import android.annotation.SuppressLint;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ycs.servicetest.R;
import com.ycs.servicetest.receiver.DialogReceiver;

public class FloatWindowService extends Service {

    private TextView tv;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        showFloatingWindow();

        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            // 获取WindowManager服务
            windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
            // 新建悬浮窗控件
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.float_window, null);
            tv = view.findViewById(R.id.content);

            tv.setMovementMethod(ScrollingMovementMethod.getInstance());
            final SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
            tv.setText(sp.getString("text", ""));

            view.setOnTouchListener(new FloatingOnTouchListener());

            tv.setOnClickListener(new View.OnClickListener() {
                int i = 0;
                final long[] mHits = new long[3];

                @Override
                public void onClick(View v) {
                    System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                    mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                    if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                        i++;
                        tv.setMaxLines((i % 6) + 3);
                    }
                }
            });
            view.setOnClickListener(v -> sendBroadcast(new Intent(FloatWindowService.this, DialogReceiver.class)));
            // 设置LayoutParam
            layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.gravity = Gravity.START;
            layoutParams.x = windowManager.getDefaultDisplay().getWidth();
            layoutParams.y = -300;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            // 将悬浮窗控件添加到WindowManager

            windowManager.addView(view, layoutParams);

        }
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;


        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();

                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
//
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;

                    // 更新悬浮窗控件布局
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    int Y = (int) event.getRawY();
                    int mY = Y - y;
                    y = Y;
                    layoutParams.x = windowManager.getDefaultDisplay().getWidth();
                    layoutParams.y = layoutParams.y + mY;
                    // 更新悬浮窗控件布局
                    windowManager.updateViewLayout(view, layoutParams);
                    break;

                default:
                    break;
            }
            return false;
        }
    }
}
