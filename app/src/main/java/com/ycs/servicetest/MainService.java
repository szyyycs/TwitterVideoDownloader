package com.ycs.servicetest;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;


public class MainService extends Service {
    private ClipboardManager manager;
    private Handler h=new Handler();
    private String oldStr="";
    private String newStr="";
    private Notification.Builder builder;
    private Runnable r;
    private RelativeLayout view;
    private TextView tv;
    private WindowManager windowManager;
    private final Timer timer = new Timer();
    private WindowManager.LayoutParams layoutParams;
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                        //获取剪切板内容
                        newStr=new ClipBoardUtil(MainService.this).paste();
                        if(!newStr.equals(oldStr)&&newStr!=null&&newStr!=""){
                            builder.setContentText("粘贴内容为"+newStr);
                            Notification notification = builder.build();
                            notification.priority=Notification.PRIORITY_HIGH;
//                            tv.setText("粘贴内容为"+newStr);
//                            windowManager.updateViewLayout(view,layoutParams);
                            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            nm.notify(110, notification);
                            Toast.makeText(MainService.this,newStr,Toast.LENGTH_SHORT).show();
                            Log.e("杨", newStr);
                            oldStr=newStr;
                        }




            }

            super.handleMessage(msg);
        }
    };
    public MainService() {

    }
    @Override
    public void onCreate() {
        super.onCreate();
//        r=new Runnable() {
//            @Override
//            public void run() {
//                newStr=new ClipBoardUtil(MainService.this).paste();
//                //Toast.makeText(MainService.this, newStr, Toast.LENGTH_SHORT).show();
//                if(!newStr.equals(oldStr)&&newStr!=null&&newStr!=""){
//                    builder.setContentText("粘贴内容为"+newStr);
//                    Notification notification = builder.build();
//                    notification.priority=Notification.PRIORITY_HIGH;
//
//                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                    nm.notify(110, notification);
////                    tv.setText("粘贴内容为"+newStr);
////                    windowManager.updateViewLayout(view,layoutParams);
//                    Toast.makeText(MainService.this,newStr,Toast.LENGTH_SHORT).show();
//                    Log.e("杨", newStr);
//                    oldStr=newStr;
//                }
//                h.postDelayed(this, 500);
//            }
//        };
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
                //showFloatingWindow();
                String CHANNEL_ONE_ID = "com.ycs.cn";
                String CHANNEL_ONE_NAME = "Channel One";
                NotificationChannel notificationChannel = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                            CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.setShowBadge(true);
                    notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.createNotificationChannel(notificationChannel);
                }
                builder=new Notification.Builder(MainService.this.getApplicationContext());
                Intent it=new Intent(MainService.this,MainActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder.setChannelId(CHANNEL_ONE_ID);
                }
                final RemoteViews views = new RemoteViews(getPackageName(),R.layout.notification_view);
                builder.setContentIntent(PendingIntent.getActivity(MainService.this,0,it,
                        0))
                        .setContentTitle("提示")
////                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.large_icon))
                        .setSmallIcon(R.mipmap.app_icon)
//                        .setContentText("粘贴的内容是"+new ClipBoardUtil(MainService.this).paste())
                        //.setContent(views)
                        .setWhen(System.currentTimeMillis());
                final Intent intentInput=new Intent(this,DialogReceiver.class);
                final PendingIntent pi = PendingIntent.getBroadcast(this,0,intentInput,PendingIntent.FLAG_UPDATE_CURRENT);
                views.setTextViewText(R.id.input,"粘贴内容为"+new ClipBoardUtil(MainService.this).paste());
                final Notification notification = builder.build();
                notification.bigContentView=views;
                views.setOnClickPendingIntent(R.id.input, pi);
                notification.iconLevel=1000;
                startForeground(110, notification);
                if(Build.VERSION.SDK_INT < 29){
                    manager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                    manager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
                        @Override
                        public void onPrimaryClipChanged() {
                            String msg=new ClipBoardUtil(MainService.this).paste();
                            views.setTextViewText(R.id.input,"粘贴内容为"+new ClipBoardUtil(MainService.this).paste());
                            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            nm.notify(110, notification);
                            if(msg.equals("o")){
                                sendBroadcast(intentInput);
                            }
                            Toast.makeText(MainService.this,msg,Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    String msg=new ClipBoardUtil(MainService.this).paste();
                    views.setTextViewText(R.id.input,"点击此处输入链接");
                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(110, notification);
                    if(msg.equals("o")){
                        sendBroadcast(intentInput);
                    }
                }

                return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            // 获取WindowManager服务
            windowManager= (WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE);

            // 新建悬浮窗控件
            LayoutInflater inflater =(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            view= (RelativeLayout) inflater.inflate(R.layout.float_window,null);
            tv=view.findViewById(R.id.content);
            tv.setText("粘贴内容为"+new ClipBoardUtil(MainService.this).paste());
            view.setOnTouchListener(new FloatingOnTouchListener());
            // 设置LayoutParam
            layoutParams= new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.format = PixelFormat.RGBA_8888;
//            layoutParams.width = 300;
//            layoutParams.height = 200;
            layoutParams.gravity= Gravity.LEFT;
            layoutParams.x  = windowManager.getDefaultDisplay().getWidth();
            layoutParams.y = (int) getResources().getDimension(R.dimen.dp_m_90);
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
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
                default:
                    break;
            }
            return false;
        }
    }


}
