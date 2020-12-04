package com.ycs.servicetest;

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

import static com.ycs.servicetest.WebUtil.isHttpUrl;

public class MainActivity extends AppCompatActivity {
    final static String TAG="yang";
    private RelativeLayout floatWindow;
    private Button btn;
    private EditText etInput;

    private String[] permissions = {Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private ImageView iv;

    private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==1){
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
        floatWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,FloatActivity.class);

                startActivity(intent);

            }
        });
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadingUtil.Loading_show(MainActivity.this);
                Intent i=new Intent(MainActivity.this, VideoActivity.class);
                startActivity(i);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, permissions,199);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(MainActivity.this, MainService.class));

        }else{
            startService(new Intent(MainActivity.this, MainService.class));

        }
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
                    Message ms=new Message();
                    ms.what=1;
                    ms.obj=text;
                    handler.sendMessage(ms);
                    //Toast.makeText(MainActivity.this, "开始"+text, Toast.LENGTH_SHORT).show();
                }else if(!WebUtil.isNetworkConnected(MainActivity.this)){
                    Toast.makeText(MainActivity.this, "网络未打开", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "您粘贴的不是网址噢", Toast.LENGTH_SHORT).show();
                }
            }
        });



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

    @Override
    protected void onResume() {
        super.onResume();
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