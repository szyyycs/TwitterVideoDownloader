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
import android.opengl.Visibility;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
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
import androidx.core.app.NotificationCompat;

import com.downloader.PRDownloader;
import com.downloader.Status;

import java.util.Timer;


import static com.ycs.servicetest.WebUtil.downloadId;
import static com.ycs.servicetest.WebUtil.isHttpUrl;


public class MainService extends Service {
    private ClipboardManager manager;
    private Handler h = new Handler();
    private String oldStr = "";
    static Notification notification;
    static RemoteViews views;
    private Runnable r;
    private RelativeLayout view;
    private TextView tv;
    private WindowManager windowManager;
    private final Timer timer = new Timer();
    private WindowManager.LayoutParams layoutParams;
    public static NotificationManager nm;
    private DialogReceiver dialogReceiver;

    //    private TimerTask task = new TimerTask() {
//        @Override
//        public void run() {
//            Message message = new Message();
//            message.what = 1;
//            handler.sendMessage(message);
//        }
//    };
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if(msg.what==1){
//                        //获取剪切板内容
//                        newStr=new ClipBoardUtil(MainService.this).paste();
//                        if(!newStr.equals(oldStr)&&newStr!=null&&newStr!=""){
//                            builder.setContentText("粘贴内容为"+newStr);
//                            Notification notification = builder.build();
//                            notification.priority=Notification.PRIORITY_HIGH;
////                            tv.setText("粘贴内容为"+newStr);
////                            windowManager.updateViewLayout(view,layoutParams);
//                            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                            nm.notify(110, notification);
//                            Toast.makeText(MainService.this,newStr,Toast.LENGTH_SHORT).show();
//                            Log.e("杨", newStr);
//                            oldStr=newStr;
//                        }
//
//
//
//
//            }
//
//            super.handleMessage(msg);
//        }
//    };
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
        nm= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(MainService.this.getApplicationContext());
        Intent it = new Intent(MainService.this, MainActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ONE_ID);
        }

        if (Build.VERSION.SDK_INT < 29) {
            views = new RemoteViews(getPackageName(), R.layout.notification_view);
        } else {
            views = new RemoteViews(getPackageName(), R.layout.notification_view_10);
        }
        builder.setContentIntent(PendingIntent.getActivity(MainService.this, 0, it,
                0))
                .setContentTitle("提示")
////                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.large_icon))
                .setSmallIcon(R.mipmap.app_icon)
//                        .setContentText("粘贴的内容是"+new ClipBoardUtil(MainService.this).paste())
                .setWhen(System.currentTimeMillis());
        final Intent intentInput = new Intent(this, DialogReceiver.class);
       // intentInput.setAction("");
        final PendingIntent pi = PendingIntent.getBroadcast(this, 0, intentInput, PendingIntent.FLAG_UPDATE_CURRENT);
        notification = builder.build();
        if (Build.VERSION.SDK_INT < 29) {
            views.setTextViewText(R.id.input, new ClipBoardUtil(MainService.this).paste());

            notification.bigContentView = views;
        } else {
            notification.contentView = views;
        }

        views.setOnClickPendingIntent(R.id.input, pi);
        notification.contentIntent = pi;
        notification.iconLevel = 1000;
        startForeground(110, notification);
        if (Build.VERSION.SDK_INT < 29) {
            manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            manager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    String msg = new ClipBoardUtil(MainService.this).paste();
                    views.setTextViewText(R.id.input, new ClipBoardUtil(MainService.this).paste());
                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(110, notification);
                    //Toast.makeText(MainService.this, msg, Toast.LENGTH_SHORT).show();
                    if (isHttpUrl(msg)&&WebUtil.isNetworkConnected(MainService.this)) {
                        if(PRDownloader.getStatus(downloadId)== Status.RUNNING&&PRDownloader.getStatus(downloadId)== Status.PAUSED){
                            Toast.makeText(MainService.this, "正在下载中，请等会再下载", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent i = new Intent(MainService.this, WebService.class);
                        i.putExtra("url",msg);
                        startService(i);
                    }
                }
            });
        } else {
            //String msg = new ClipBoardUtil(MainService.this).paste();
//                    views.setTextViewText(R.id.input,"点击此处输入链接");
            //NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(110, notification);
        }

        return super.onStartCommand(intent, flags, startId);
    }

//

    public static RemoteViews getContentView(Context context, Notification notification) {
        if (notification.contentView != null)
            return notification.contentView;
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Notification.Builder.recoverBuilder(context, notification).createContentView();
        else
            return null;
    }

    public static void updateNotification(Context context, String msg) {
        views.setTextViewText(R.id.input, msg);

//        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(110, notification);
    }
    public static void updateTitle( String msg) {
        views.setTextViewText(R.id.title, msg);

//        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(110, notification);
    }
    static int progress=0;
    public synchronized static void updateProgress(Context context, int percent) {
        if(progress==percent){
            return;
        }
        progress=percent;
        if(percent==100){
            Log.e("yyu","成功");
            views.setViewVisibility(R.id.input, View.VISIBLE);
            views.setViewVisibility(R.id.pb, View.GONE);
        }else {
            views.setViewVisibility(R.id.input, View.GONE);
            views.setViewVisibility(R.id.pb, View.VISIBLE);
            views.setProgressBar(R.id.pb,100,percent,false);
        }
        nm.notify(110, notification);
    }
    public synchronized static void update(String msg){
        views.setViewVisibility(R.id.input, View.VISIBLE);
        views.setViewVisibility(R.id.pb, View.GONE);
        views.setTextViewText(R.id.input,msg);
        nm.notify(110, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dialogReceiver);
    }

    public static void setTextMarquee(TextView textView) {
        if (textView != null) {
            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textView.setSingleLine(true);
            textView.setSelected(true);
            textView.setFocusable(true);
            textView.setFocusableInTouchMode(true);
        }
    }
    
}
