package com.ycs.servicetest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    final static String TAG="yang";
    public Handler h;
    private Button btn;
    private String[] permissions = {Manifest.permission.SYSTEM_ALERT_WINDOW};
//    private final Timer timer = new Timer();
//    private TimerTask task = new TimerTask() {
//        @Override
//        public void run() {
//            Message message = new Message();
//            message.what = 1;
//            handler.sendMessage(message);
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            }
        }
        startService(new Intent(MainActivity.this, MainService.class));
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==1){
                    getWindow().getDecorView().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, new ClipBoardUtil(MainActivity.this).paste(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                    //startService(new Intent(MainActivity.this, MainService.class));
                }
            }
        }
        if(requestCode==199){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.SYSTEM_ALERT_WINDOW)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "未允许权限", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this, permissions,199);
                }
            }
        }
    }
    public void startService(){
        Intent intent=new Intent(this,MainService.class);
        startService(intent);
    }
}