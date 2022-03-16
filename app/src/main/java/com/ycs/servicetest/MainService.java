package com.ycs.servicetest;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.ycs.servicetest.utils.ClipBoardUtil;
import com.ycs.servicetest.utils.WebUtil;

import java.util.Timer;

import static com.ycs.servicetest.utils.WebUtil.analyzeList;
import static com.ycs.servicetest.utils.WebUtil.isDownloading;
import static com.ycs.servicetest.utils.WebUtil.isHttpUrl;


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

    public MainService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
                .setSmallIcon(R.mipmap.app_icon)
                .setWhen(System.currentTimeMillis());
        final Intent intentInput = new Intent(this, DialogReceiver.class);
        final PendingIntent pi = PendingIntent.getBroadcast(this, 0, intentInput, PendingIntent.FLAG_UPDATE_CURRENT);
        notification = builder.build();
        if (Build.VERSION.SDK_INT < 29) {
            views.setTextViewText(R.id.input, new ClipBoardUtil(MainService.this).paste());
            notification.bigContentView = views;
        } else {
            notification.contentView = views;
        }
        Intent ii=new Intent(MainService.this,MainActivity.class);
        ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pii=PendingIntent.getActivity(this,2,ii,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.small_icon,pii);
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
                    if (isHttpUrl(msg)&&msg.contains("twitter")) {
                        if(isDownloading|| WebUtil.isAnalyse){
                            if(!analyzeList.contains(msg)){
                                WebUtil.analyzeList.add(msg);
                                Log.e("yyy", "analyzeList的值："+analyzeList.toString() );
                                Toast.makeText(MainService.this, "已加入下载列表", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MainService.this, "已在下载队列中", Toast.LENGTH_SHORT).show();
                                Log.e("yyy", "analyzeList的值："+analyzeList.toString() );
                            }
                            return;
                        }
                        Intent i = new Intent(MainService.this, WebService.class);
                        i.putExtra("url",msg);
                        startService(i);
                    }
                }
            });
        } else {
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
        nm.notify(110, notification);
    }
    public static void updateTitle( String msg) {
        views.setTextViewText(R.id.title, msg);
        nm.notify(110, notification);
    }
    static int progress=0;
    public synchronized static void updateProgress(Context context, int percent,String now) {
        if(progress==percent){
            return;
        }
        progress=percent;
        if(percent%2==1){
            return;
        }
        if(percent==100){
            Log.e("yyu","成功");
            views.setViewVisibility(R.id.input, View.VISIBLE);
            views.setTextViewText(R.id.input,"下载已完成");
            views.setViewVisibility(R.id.pb, View.GONE);
            views.setViewVisibility(R.id.progress_num,View.GONE);
        }else {
            views.setViewVisibility(R.id.progress_num,View.VISIBLE);
            views.setTextViewText(R.id.progress_num,now);
            views.setViewVisibility(R.id.input, View.GONE);
            views.setViewVisibility(R.id.pb, View.VISIBLE);
            if(percent>50){
                views.setTextColor(R.id.progress_num,context.getColor(R.color.colorwhite));
            }
            views.setProgressBar(R.id.pb,100,percent,false);
        }
        nm.notify(110, notification);
    }
    public synchronized static void update(String msg){
        views.setViewVisibility(R.id.input, View.VISIBLE);
        views.setViewVisibility(R.id.pb, View.GONE);
        views.setViewVisibility(R.id.progress_num,View.GONE);
        views.setTextViewText(R.id.input,msg);
        nm.notify(110, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(dialogReceiver!=null){
            unregisterReceiver(dialogReceiver);
        }

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
