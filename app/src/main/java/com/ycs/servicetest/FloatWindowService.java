package com.ycs.servicetest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
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
import androidx.annotation.RequiresApi;

import de.hdodenhof.circleimageview.CircleImageView;

public class FloatWindowService extends Service {

    private RelativeLayout view;
    private RelativeLayout view2;
    private TextView tv;
    private CircleImageView civ;
    private TextView y;
    private int Y=0;
    private Boolean flag=true;
    private ImageView line;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sharedPreferences = getSharedPreferences("date", Context.MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showFloatingWindow();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            // 获取WindowManager服务
            windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);

            // 新建悬浮窗控件
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            view = (RelativeLayout) inflater.inflate(R.layout.float_window, null);
            tv = view.findViewById(R.id.content);
            //y=view.findViewById(R.id.y);
//            y.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(FloatWindowService.this, "嘿嘿", Toast.LENGTH_SHORT).show();
//                }
//            });

            tv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    Y=oldScrollY;
                    Log.e("滚动", "Y"+Y );
                   // Log.e("滚动","scrollX:"+scrollX+"\nscrollY:"+scrollX+"\noldScrollX:"+oldScrollX+"\noldScrollY:"+oldScrollY);
                }
            });
            line=view.findViewById(R.id.line);
            tv.setMovementMethod(ScrollingMovementMethod.getInstance());
            final SharedPreferences sp=getSharedPreferences("data", Context.MODE_PRIVATE);
            tv.setText(sp.getString("text",""));
            civ=view.findViewById(R.id.civ);
//            view2= (RelativeLayout) inflater.inflate(R.layout.float_window, null);
            view.setOnTouchListener(new FloatingOnTouchListener());
//            line.setOnTouchListener(new View.OnTouchListener() {
//                int y;
//                int height;
//                WindowManager.LayoutParams layoutParam1 = new WindowManager.LayoutParams();
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    switch (event.getAction()){
//                        case MotionEvent.ACTION_DOWN:
//                            height = tv.getLayoutParams().height;
//                            y = (int) event.getRawY();
//                            break;
//                        case MotionEvent.ACTION_MOVE:
//                            int nowY = (int) event.getRawY();
//                            int movedY = nowY - y;
//                           // layoutParam1.height = layoutParam1.height + movedY;
//                           // layoutParam1.y=layoutParam1.y+movedY;
//                            layoutParams.height=layoutParams.height + movedY;
//                           // windowManager.updateViewLayout(view, layoutParams);
//                            windowManager.updateViewLayout(line, layoutParams);
//                            break;
//                        default:
//                            break;
//                    }
//                    return false;
//                }
//            });

            tv.setOnClickListener(new View.OnClickListener() {
                int i=0;
                long[] mHits = new long[3];
                @Override
                public void onClick(View v) {
                    System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                    mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                    if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                        i++;
                        tv.setMaxLines((i%6)+3);
                    }
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                private int i=0;
                @Override
                public void onClick(View v) {
                    i=0;
                    if(flag){
                        flag=false;
                        tv.setVisibility(View.VISIBLE);
                        tv.setText(sp.getString("text",""));

                        tv.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if(event.getAction()==MotionEvent.ACTION_UP&&i==0){
                                    Log.e("抬起","嘿嘿");
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv.scrollTo(0,Y);
                                            i=1;
                                        }
                                    },200);

                                }
                                return false;
                            }
                        });
                        tv.scrollTo(0,Y);
//                        line.setVisibility(View.VISIBLE);
                        layoutParams.x = windowManager.getDefaultDisplay().getWidth();
                        layoutParams.width= WindowManager.LayoutParams.MATCH_PARENT;
                        windowManager.updateViewLayout(view, layoutParams);
                    }else{
                        flag=true;
                        windowManager.removeView(view);
                        //Y=tv.getScrollY();
                        Log.e("滚动数", Y+"" );
                       // line.setVisibility(View.INVISIBLE);
                        tv.setVisibility(View.GONE);
                        layoutParams.x = windowManager.getDefaultDisplay().getWidth();
//                        windowManager.updateViewLayout(view, layoutParams);
                        layoutParams.width= (int) getResources().getDimension(R.dimen.dp_20);
                        windowManager.addView(view, layoutParams);
                    }

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
            layoutParams.gravity = Gravity.LEFT;
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
        private int height;
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();

                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
//                    if(tv.getVisibility()==View.VISIBLE){
//                        int nowY = (int) event.getRawY();
//                        int movedY = nowY - y;
//                        layoutParams.height = layoutParams.height + movedY;
//                        windowManager.updateViewLayout(view, layoutParams);
//                        windowManager.updateViewLayout(tv,layoutParams);
//                        break;
//                    }
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
