package com.ycs.servicetest.service;


import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.ycs.servicetest.R;
import com.ycs.servicetest.receiver.DialogReceiver;

public class DownLoadWindowService extends Service {
    private static RelativeLayout view;
    private static ImageView civ;
    private final Handler handler = new Handler();
    public static CircleProgressBar ircleProgressBar;
    private static WindowManager windowManager;
    private static WindowManager.LayoutParams layoutParams;

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        if (windowManager != null) {
            windowManager.removeView(view);
        }
    }

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            // 获取WindowManager服务
            windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
            // 新建悬浮窗控件
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            view = (RelativeLayout) inflater.inflate(R.layout.float_download, null);
            civ = view.findViewById(R.id.civ);
            ircleProgressBar = view.findViewById(R.id.progress);
            ircleProgressBar.setProgress(80);
            ircleProgressBar.setVisibility(View.GONE);
            view.setOnTouchListener(new FloatingOnTouchListener());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendBroadcast(new Intent(DownLoadWindowService.this, DialogReceiver.class));
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
            layoutParams.x = windowManager.getDefaultDisplay().getWidth();
            layoutParams.y = -getResources().getDimensionPixelSize(R.dimen.dp_100);
            layoutParams.width = getResources().getDimensionPixelSize(R.dimen.dp_40);
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.dp_40);
            windowManager.addView(view, layoutParams);

            setViewFade();
        }
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;
        private int xx;
        private int yy;
        private boolean isLeft;
        TextView tvRight = view.findViewById(R.id.right);
        TextView tvLeft = view.findViewById(R.id.left);
        private final long[] mHits = new long[2];

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    xx = x;
                    yy = (int) event.getRawY();
                    y = (int) event.getRawY();
                    view.setAlpha(1);
                    handler.removeCallbacks(runnable);
                    isLeft = tvLeft.getVisibility() == View.VISIBLE;
                    tvLeft.setVisibility(View.GONE);
                    tvRight.setVisibility(View.GONE);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - windowManager.getDefaultDisplay().getWidth() / 2;
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
                    if (Math.abs(X - xx) < 1.5 && Math.abs(Y - yy) < 1.5) {
                        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);

                        sendBroadcast(new Intent(DownLoadWindowService.this, DialogReceiver.class));
                        setViewFade();
                        if (isLeft) {
                            tvLeft.setVisibility(View.VISIBLE);
                        } else {
                            tvRight.setVisibility(View.VISIBLE);
                        }
                        return true;
                    }
                    if (layoutParams.x + mX > 0) {
                        layoutParams.x = windowManager.getDefaultDisplay().getWidth();
                        tvRight.setVisibility(View.VISIBLE);
                    } else {
                        layoutParams.x = -windowManager.getDefaultDisplay().getWidth();
                        tvLeft.setVisibility(View.VISIBLE);
                    }
                    layoutParams.y = layoutParams.y + mY;
                    setViewFade();
                    // 更新悬浮窗控件布局
                    windowManager.updateViewLayout(view, layoutParams);
                    return true;
                default:
                    break;
            }
            return false;
        }
    }

    private void setViewFade() {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 2000);
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            fade(view);
        }
    };

    public static void updateProgress(int progress) {
        if (ircleProgressBar != null) {
            ircleProgressBar.setVisibility(View.VISIBLE);
            ircleProgressBar.setProgress(progress);
        }

        civ.setVisibility(View.GONE);

        windowManager.updateViewLayout(view, layoutParams);
    }

    public static void recover() {
        if (ircleProgressBar != null) {
            ircleProgressBar.setVisibility(View.GONE);
        }
        civ.setVisibility(View.VISIBLE);
    }

    private void fade(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1, 0.5f);
        animator.setDuration(1000);
        animator.start();
    }


}
