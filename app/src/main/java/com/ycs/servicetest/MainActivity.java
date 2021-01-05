package com.ycs.servicetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.downloader.PRDownloader;

import java.util.List;

import static com.ycs.servicetest.WebUtil.isAnalyse;
import static com.ycs.servicetest.WebUtil.isHttpUrl;

public class MainActivity extends AppCompatActivity {
    final static String TAG="yyy";
    final static int GOTO_DOWNLOAD=1;
    private RelativeLayout floatWindow;
    private Button btn;
    private EditText etInput;
    private Boolean isFloatWindowsshow=false;
    private String[] permissions = {Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW};
    private ImageView iv;

    private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==GOTO_DOWNLOAD){
                    WebUtil.isAnalyse=true;
                    MainService.updateNotification(MainActivity.this,"链接正在解析中...");
                    Intent i=new Intent(MainActivity.this,WebService.class);
                    i.putExtra("url",msg.obj.toString());
                    startService(i);
                }
                super.handleMessage(msg);
            }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        PRDownloader.initialize(getApplicationContext());
        iv=findViewById(R.id.download);
        TextView tv=findViewById(R.id.btn);
        btn=findViewById(R.id.confirm);
        etInput=findViewById(R.id.input);
        floatWindow=findViewById(R.id.floatWindow);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Intent i=new Intent(MainActivity.this, VideoActivity.class);
                startActivity(i);
            }
        });
//        iv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Intent i=new Intent(MainActivity.this, Test.class);
//                startActivity(i);
//                return false;
//            }
//        });
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!Settings.canDrawOverlays(this)) {
//                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
//                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
//            }
//            if (ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED)
//            {
//                ActivityCompat.requestPermissions(this, permissions,199);
//            }
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(MainActivity.this, MainService.class));
        }else{
            startService(new Intent(MainActivity.this, MainService.class));
        }
        WebUtil.init(this);

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etInput.getHint()!="在这粘贴链接"){
                    btn.setText("下载");
                } else{
                    btn.setText("粘贴");

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=etInput.getText().toString();
                if(text.isEmpty()){
                    text=etInput.getHint().toString();
                }
                if(isHttpUrl(text)){
                    if(isAnalyse){
                        Toast.makeText(MainActivity.this, "现在在分析链接啦，等会再下载", Toast.LENGTH_SHORT).show();
                    }else{
                        Message ms=new Message();
                        ms.what=GOTO_DOWNLOAD;
                        ms.obj=text;
                        handler.sendMessage(ms);
                    }
                }else if(!WebUtil.isNetworkConnected(MainActivity.this)){
                    Toast.makeText(MainActivity.this, "网络未打开", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "您粘贴的不是网址噢", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Intent intent = new Intent(MainActivity.this, FloatActivity.class);
//                startActivity(intent);
                /**
                 *
                 * 把onclicklistener注释取消，下面注释去掉
                 *
                 * */
                if(!isFloatWindowsshow){
                    Intent i=new Intent(MainActivity.this,FloatWindowService.class);
                    startService(i);
                    isFloatWindowsshow=true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: 一次" );
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==111){
            Log.e("yyy","11111");
            //Toast.makeText(this, "yyy", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "未允许权限，同意后才可以使用本APP", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this, permissions,111);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "请允许悬浮窗权限", Toast.LENGTH_SHORT);
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, permissions,111);
            }
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                getWindow().getDecorView().post(new Runnable() {
                    @Override
                    public void run() {
                        String paste=new ClipBoardUtil(MainActivity.this).paste();
                        etInput.setHint(paste);
                        if(etInput.getHint().toString().equals("在这粘贴链接")){
                            btn.setText("粘贴");
                        } else{
                            btn.setText("下载");
                        }
                    }
                });
            }
        });
    }
}