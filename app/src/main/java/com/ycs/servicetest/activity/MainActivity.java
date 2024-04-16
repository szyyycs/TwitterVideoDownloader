package com.ycs.servicetest.activity;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;
import static com.ycs.servicetest.common.Constant.REQUEST_CODE;
import static com.ycs.servicetest.utils.WebUtil.analyzeList;
import static com.ycs.servicetest.utils.WebUtil.isAnalyse;
import static com.ycs.servicetest.utils.WebUtil.isDownloading;
import static com.ycs.servicetest.utils.WebUtil.isHttpUrl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.listener.OnPageChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.tencent.mmkv.MMKV;
import com.ycs.servicetest.MainApplication;
import com.ycs.servicetest.R;
import com.ycs.servicetest.common.Constant;
import com.ycs.servicetest.common.KVKey;
import com.ycs.servicetest.service.DownLoadWindowService;
import com.ycs.servicetest.service.MainService;
import com.ycs.servicetest.service.WebService;
import com.ycs.servicetest.utils.ClipBoardUtil;
import com.ycs.servicetest.utils.KVUtil;
import com.ycs.servicetest.utils.StatusBarUtil;
import com.ycs.servicetest.utils.WebUtil;
import com.ycs.servicetest.view.CustomImageDialog;
import com.ycs.servicetest.view.CustomIosAlertDialog;

import java.util.Calendar;
import java.util.Objects;

import io.flutter.embedding.android.FlutterActivity;

//import com.hjq.permissions.OnPermissionCallback;
//import com.hjq.permissions.Permission;
//import com.hjq.permissions.XXPermissions;


public class MainActivity extends AppCompatActivity {
    final static int GOTO_DOWNLOAD = 1;

    private Button btn;
    private EditText etInput;
    private Boolean isFloatWindowsshow = false;
    private final String[] permissions = {SYSTEM_ALERT_WINDOW,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private final String[] xpermissions = {
            SYSTEM_ALERT_WINDOW,
            MANAGE_EXTERNAL_STORAGE
    };
    private ImageView ivDownloadFile;

    //跳到下载
    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == GOTO_DOWNLOAD) {
                //WebUtil.isAnalyse=true;
                MainService.updateNotification(MainActivity.this, "链接正在解析中...");
                Intent i = new Intent(MainActivity.this, WebService.class);
                i.putExtra("url", msg.obj.toString());
                startService(i);
            }

            super.handleMessage(msg);
        }
    };

    //显示倒计时dialog
    void showSurpriseDialog() {
        CustomImageDialog d = new CustomImageDialog(MainActivity.this).builder();

        d.show();
        handler.postDelayed(() -> runOnUiThread(() -> d.setAni(R.mipmap.two)), 1000);
        handler.postDelayed(() -> runOnUiThread(() -> d.setAni(R.mipmap.one)), 2000);
        handler.postDelayed(() -> startActivity(
                FlutterActivity.createDefaultIntent(Objects.requireNonNull(MainApplication.Companion.getAppContext()))
        ), 3000);
        handler.postDelayed(d::dismiss, 4000);

    }

    //检查时间
    void checkTime() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        MMKV kv = KVUtil.Companion.getMMKV(KVKey.BIRTH_DAY);
        for (String key : kv.allKeys()) {
            String value = kv.decodeString(key);
            String[] values = value.split("\\.");
            int month_saved = Integer.parseInt(values[0]);
            int day_saved = Integer.parseInt(values[1]);
            if (month == month_saved && day == day_saved) {
                handler.postDelayed(this::showSurpriseDialog, 1000);
                return;
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        checkTime();
        StatusBarUtil.INSTANCE.setStatusBarColor(this, R.color.colorIosBlue, false);
        getPermission();
        //getXXPermission();
        showNotification();
        initView();
        showFloatWindows();
        checkPermission();
        showGuide();
    }


    private void showGuide() {
        NewbieGuide.with(MainActivity.this)
                .setLabel("guide1")
                .setShowCounts(1)//控制次数
                .addGuidePage(
                        GuidePage.newInstance()
                                .addHighLight(etInput, HighLight.Shape.ROUND_RECTANGLE, 50, 15, null)
                                .setLayoutRes(R.layout.tip_btn)
                )
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(ivDownloadFile, HighLight.Shape.CIRCLE, 0, 5, null)
                        .setLayoutRes(R.layout.tip_download))
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {
                        Window window = getWindow();
                        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorIosBlueDark));
                    }

                    @Override
                    public void onRemoved(Controller controller) {
                        Window window = getWindow();
                        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorIosBlue));
                    }
                })
                .setOnPageChangedListener(new OnPageChangedListener() {
                    @Override
                    public void onPageChanged(int page) {

                    }
                })
                .show();

    }

    //    public void getXXPermission(){
//        if(!XXPermissions.isGranted(context,permissions)){
//            Toast.makeText(context, "请授权悬浮窗以及存储权限", Toast.LENGTH_SHORT).show();
//            XXPermissions.with(this)
//                    .permission(Permission.SYSTEM_ALERT_WINDOW)
//                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
//                    .request(new OnPermissionCallback() {
//                        @Override
//                        public void onGranted(List<String> permissions, boolean all) {
//                            if (!Settings.canDrawOverlays(context)) {
//                                //Toast.makeText(context, "未允许权限，同意后才可以使用本APP，请同意权限", Toast.LENGTH_SHORT).show();
//                                XXPermissions.startPermissionActivity(MainActivity.this, permissions);
//                                Toast.makeText(context, "悬浮窗授权失败", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(context, "授权成功", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onDenied(List<String> permissions, boolean never) {
//                            if (never) {
//                                Toast.makeText(context, "未允许权限，同意后才可以使用本APP，请同意权限", Toast.LENGTH_SHORT).show();
//                                XXPermissions.startPermissionActivity(MainActivity.this, permissions);
//                            } else {
//                                getXXPermission();
//                            }
//                        }
//                    });
//        }
//
//    }
    public void initView() {
        ivDownloadFile = findViewById(R.id.download);
        btn = findViewById(R.id.confirm);
        etInput = findViewById(R.id.input);

        ivDownloadFile.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, VideoListActivity.class);
            startActivity(i);
        });

        btn.setOnClickListener(v -> {
            String text = etInput.getText().toString();
            if (text.isEmpty()) {
                text = etInput.getHint().toString();
            }

            if (!WebUtil.isNetworkConnected(getApplicationContext())) {
                Toast.makeText(MainActivity.this, "网络未打开", Toast.LENGTH_SHORT).show();
            } else if (isHttpUrl(text) && text.contains("twitter")) {
                if (isAnalyse || isDownloading) {
                    if (!analyzeList.contains(text)) {
                        WebUtil.analyzeList.add(text);
                        Log.e(Constant.TAG, "analyzeList的值：" + analyzeList);
                        Toast.makeText(MainActivity.this, "已加入下载列表", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(Constant.TAG, "analyzeList的值：" + analyzeList);
                        Toast.makeText(MainActivity.this, "已在下载列表中", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Message ms = new Message();
                    ms.what = GOTO_DOWNLOAD;
                    ms.obj = text;
                    handler.sendMessage(ms);

                }
            } else {
                Toast.makeText(MainActivity.this, "您粘贴的不是网址噢", Toast.LENGTH_SHORT).show();
            }
        });
        btn.setOnLongClickListener(v -> {
            //  startActivity(new Intent(MainActivity.this, PubuActivity.class));
            startActivity(FlutterActivity.createDefaultIntent(MainActivity.this));
            Log.d(Constant.TAG, "initView: 长安了");
            return true;
        });
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etInput.getHint() != "在这粘贴链接") {
                    btn.setText("下载");
                } else {
                    btn.setText("粘贴");

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(MainActivity.this, MainService.class));
        } else {
            startService(new Intent(MainActivity.this, MainService.class));
        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

    public void showFloatWindows() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!isFloatWindowsshow) {
                Intent i = new Intent(MainActivity.this, DownLoadWindowService.class);
                try {
                    startService(i);
                    isFloatWindowsshow = true;
                } catch (Exception ignored) {

                }

            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(Constant.TAG, "onActivityResult: 一次");
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败，请重新授权", Toast.LENGTH_SHORT).show();
                getPermission();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                isFloatWindowsshow = false;
                showFloatWindows();
                //startService(new Intent(MainActivity.this, MainService.class));
            }
        } else if (requestCode == REQUEST_CODE) {
            if (Environment.isExternalStorageManager()) {
                Toast.makeText(this, "已获得所有权限", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "暂未取得读取文件权限，请前往获取", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissionss, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissionss, grantResults);
        if (requestCode == 111) {
            Log.e("yyy", "获取权限返回");
            //Toast.makeText(this, "yyy", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "未允许权限，同意后才可以使用本APP", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, permissions, 111);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(new Runnable() {
            @Override
            public void run() {
                getWindow().getDecorView().post(() -> {
                    String paste = new ClipBoardUtil(getApplicationContext()).paste();
                    etInput.setHint(paste);
                    if (etInput.getHint().toString().equals("在这粘贴链接")) {
                        btn.setText("粘贴");
                    } else {
                        btn.setText("下载");
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(MainActivity.this, DownLoadWindowService.class));
        stopService(new Intent(MainActivity.this, MainService.class));

//        webView.clearCache(true);
//        webView.clearHistory();
//        webView.destroy();
    }

    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!Settings.canDrawOverlays(this)) {
//                Toast.makeText(this, "请允许悬浮窗权限", Toast.LENGTH_SHORT);
                new CustomIosAlertDialog(this)
                        .builder()
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .setTitle("前往获取悬浮窗权限")
                        .setMsg("请在应用列表中找到VideoDownload，并允许显示在其他应用上层")
                        .setPositiveButton("立即跳转", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + getPackageName())), 0);
                            }
                        })

                        .show();

            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 111);
        }
    }
}