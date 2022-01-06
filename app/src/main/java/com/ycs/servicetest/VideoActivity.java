package com.ycs.servicetest;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.disklrucache.DiskLruCache;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.tencent.mmkv.MMKV;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.ycs.servicetest.Constant.REQUEST_CODE;

//
//import com.geccocrawler.gecco.annotation.Gecco;
//import com.geccocrawler.gecco.annotation.RequestParameter;
//import com.geccocrawler.gecco.annotation.Text;


public class VideoActivity extends AppCompatActivity {
    //    private WebView webView;
//    private TextView tv;
//    private TextView tvWeb;
    private ItemAdapter adapter;
    private Boolean isScaning = false;
    private Boolean canChange = false;
    private CustomLinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArrayList<Items> itemsList = new ArrayList<>();
    private ArrayList<Items> newItemsList = new ArrayList<>();
    //private ArrayList<String> srcList=new ArrayList<>();
    //private VideoView vv;
    private MyVideoPlayer detailPlayer;
    private boolean isPlay = false;
    private boolean isPause;
    private RelativeLayout blank;
    private ImageView iv_intotiktok;
    private OrientationUtils orientationUtils;
    private View scan;
    private ImageView toScan;

    private TextView scanNum;
    //private String url=Environment.getExternalStorageDirectory() +"/123/";
    //private String url=Environment.getExternalStorageDirectory() +"/DCIM/Camera/";
    private String url = Environment.getExternalStorageDirectory() + "/.savedPic/";
    private boolean isFullScreen = false;
    private ImageView iv;
    private DiskLruCache mDiskCache;
    private Boolean HaveList = false;
    //private ImageView blank;
    public static final int SEARCH_VIDEO = 1;
    public static final int SEARCH_ONE_VIDEO = 2;
    public static final int SCANING_ONE_PIC = 3;
    public static final int AFTER_SORT_SCAN = 4;
    public static final int UPDATE_ALL = 5;
    public static final int UPDATE_LIST = 6;
    public static final int NO_UPDATE_LIST = 7;
    private RelativeLayout title;
    //    private LruCache<String, Bitmap> mMemoryCache;
    private RelativeLayout sortImage;
    private Boolean twitterIsEmpty=false;
    private Boolean isNull = true;
    private int position = 0;
    int i[] = {0, 0, 0};
    static final String TAG = "yyy";
    private int nowPlayPosition = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEARCH_VIDEO:
                    //之前没有list，第一次扫描
                    if (itemsList.size() == 0) {
                        //list长度为空，显示空
                        isNull = true;
                        setBlankUI();
                        LoadingUtil.Loading_close();
                        scan.setVisibility(View.INVISIBLE);
                        toScan.setVisibility(View.VISIBLE);
                        scanNum.setVisibility(View.INVISIBLE);
                        break;
                    }
                    //不为空，更新列表，并开始加载日期
                    adapter.update(itemsList);
                    Toast.makeText(VideoActivity.this, "共找到" + itemsList.size() + "个视频", Toast.LENGTH_SHORT).show();
                    LoadingUtil.Loading_close();
                    isScaning = false;
                    scan.setVisibility(View.INVISIBLE);
                    toScan.setVisibility(View.VISIBLE);
                    scanNum.setVisibility(View.INVISIBLE);
                    if(kv_text.count()!=0){
                        loadPic();
                    }else{
                        loadTwitterText();
                    }
                    break;
                case UPDATE_LIST:
                    // 新扫描之后，更新列表，重新加载缩略图
                    itemsList = newItemsList;
                    adapter.update(itemsList);
                    isScaning = false;
                    Log.d(TAG, "updateList");
                    loadPic();
                    scan.setVisibility(View.INVISIBLE);
                    toScan.setVisibility(View.VISIBLE);
                    scanNum.setVisibility(View.INVISIBLE);
                    break;
                case NO_UPDATE_LIST:
                    isScaning = false;
                   // loadPic();
                    scan.setVisibility(View.INVISIBLE);
                    toScan.setVisibility(View.VISIBLE);
                    scanNum.setVisibility(View.INVISIBLE);
                    break;
                case SEARCH_ONE_VIDEO:
                    adapter.updateOne(itemsList);
                    break;
                case SCANING_ONE_PIC:
                    adapter.updateOnepic((Integer) msg.obj);
                    break;
                case AFTER_SORT_SCAN:
                    loadPicAfterSort();
                    break;
                case UPDATE_ALL:
                    adapter.update(itemsList);
                    break;
                default:
                    break;
            }
        }
    };
    private RelativeLayout r;
    private int num = 0;
    private MMKV kv;
    private MMKV kv_text;
    private boolean isSavedPic = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        getSupportActionBar().hide();
        setStatusBarColor();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){

        }
        SharedPreferences spp = getSharedPreferences("url", Context.MODE_PRIVATE);
        if (!spp.getString("url", "").equals("")) {
            if (url != spp.getString("url", "")) {
                url = spp.getString("url", "");
                isSavedPic = false;
            }

        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if (Environment.isExternalStorageManager()) {
                //Toast.makeText(this, "已获得所有权限", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "暂未取得读取文件权限，请前往获取", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
        File ff;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            ff = new File(directory, "/.savedPic/");
        }else{
            ff = new File(url);
        }
        if (!ff.exists()) {
            // 若文件夹不存在，建立文件夹
            ff.mkdirs();
        }
        try {
            mDiskCache = DiskLruCache.open(ff, 1, 1, 1024 * 1024 * 1024);
        } catch (IOException e) {
            Toast.makeText(this, "出错"+e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.getMessage() );
        }
        LoadingUtil.Loading_show(this);
        iv = findViewById(R.id.back);
        title = findViewById(R.id.title);
        scan = findViewById(R.id.scan);
        toScan = findViewById(R.id.toScan);
        toScan.setVisibility(View.INVISIBLE);
        scan.setVisibility(View.VISIBLE);
        scanNum = findViewById(R.id.scanNum);
        scanNum.setText("0");
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_intotiktok = findViewById(R.id.intoTiktok);

        iv_intotiktok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNull) {
                    Toast.makeText(VideoActivity.this, "您视频列表为空，请下载视频后再进入抖音模式哦！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                ArrayList<VideoModel> vm = new ArrayList<>();
                for (Items ii : itemsList) {
                    VideoModel vvv = new VideoModel();
                    vvv.setUrl(ii.getUrl());
                    vm.add(vvv);
                }
                Collections.shuffle(vm);
                intent.putExtra("list", vm);
                Toast.makeText(VideoActivity.this, "随机模式", Toast.LENGTH_SHORT).show();
                intent.putExtra("i", position);
                intent.setClass(VideoActivity.this, tiktok.class);
                startActivity(intent);
            }
        });
        iv_intotiktok.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isNull) {
                    Toast.makeText(VideoActivity.this, "您视频列表为空，请下载视频后再进入抖音模式哦！", Toast.LENGTH_SHORT).show();
                    return false;
                }
                Intent intent = new Intent();
                ArrayList<VideoModel> vm = new ArrayList<>();
                for (Items ii : itemsList) {
                    VideoModel vvv = new VideoModel();
                    vvv.setUrl(ii.getUrl());
                    vm.add(vvv);
                }
                Toast.makeText(VideoActivity.this, "顺序模式", Toast.LENGTH_SHORT).show();
                intent.putExtra("list", vm);

                intent.putExtra("i", position);
                intent.setClass(VideoActivity.this, tiktok.class);
                startActivity(intent);
                return false;
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        sortImage = findViewById(R.id.sort);
        sortImage.setOnClickListener(v -> {
            /*
            我的拿手好戏
            你要得意一点呢
            我的拿~手~好~戏~
            对，你不会姐会！
            对小孩子说这种女王发言
            * */
            //Toast.makeText(VideoActivity.this, "点了", Toast.LENGTH_SHORT).show();
            new XPopup.Builder(VideoActivity.this)
                    .atView(sortImage)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                    .asAttachList(new String[]{"按下载时间排序", "按视频时长排序", "按描述长度排序"},
                            new int[]{R.mipmap.downloadtime, R.mipmap.video, R.mipmap.miaoshu},
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    if (position == 0) {
                                        isScaning = false;
                                        if (i[position] % 2 == 0) {
                                            deSort(itemsList);
                                        } else {
                                            sort(itemsList);
                                        }
                                        i[position]++;
                                        adapter.update(itemsList);
                                        handler.sendEmptyMessage(AFTER_SORT_SCAN);
                                    } else if (position == 1) {
                                        isScaning = false;
                                        if (i[position] % 2 == 0) {
                                            desortByLarge(itemsList);
                                        } else {
                                            sortByLarge(itemsList);
                                        }
                                        i[position]++;
                                        adapter.update(itemsList);
                                        handler.sendEmptyMessage(AFTER_SORT_SCAN);
                                    } else if (position == 2) {
                                        isScaning = false;
                                        if (i[position] % 2 == 0) {
                                            deSortByComment(itemsList);
                                        } else {
                                            sortByComment(itemsList);
                                        }
                                        i[position]++;
                                        adapter.update(itemsList);
                                        handler.sendEmptyMessage(AFTER_SORT_SCAN);
                                    }

                                }
                            })
                    .show();


        });
        kv = MMKV.defaultMMKV();
        kv_text=MMKV.mmkvWithID("text");
        detailPlayer = findViewById(R.id.detail_player);
        detailPlayer.getTitleTextView().setVisibility(View.GONE);
        detailPlayer.getBackButton().setVisibility(View.GONE);
        orientationUtils = new OrientationUtils(this, detailPlayer);
        orientationUtils.setEnable(false);
        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption
                .setIsTouchWiget(true)
                .setRotateViewAuto(true)
                .setLockLand(false)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setLooping(true)
                //.setUrl(url)
                .setOnlyRotateLand(true)
                //.setRotateWithSystem(true)
                .setCacheWithPlay(true)
                .setVideoTitle("这里是一个竖直方向的视频")
                .setSeekRatio(1)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        orientationUtils.setEnable(detailPlayer.isRotateWithSystem());
                        isPlay = true;
                    }

                    @Override
                    public void onClickStartThumb(String url, Object... objects) {
                        super.onClickStartThumb(url, objects);
                        //hideStatusBar();
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {

                        super.onAutoComplete(url, objects);
                        isPlay = true;
                        detailPlayer.restart();
                    }

                    @Override
                    public void onPlayError(String url, Object... objects) {
                        super.onPlayError(url, objects);
                        isPlay = false;
                        Toast.makeText(VideoActivity.this, "播放错误", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        if (orientationUtils != null) {
                            orientationUtils.backToProtVideo();
                        }
                        isFullScreen = false;
                    }
                })
                .setLockClickListener(new LockClickListener() {
                    @Override
                    public void onClick(View view, boolean lock) {
                        if (orientationUtils != null) {
                            orientationUtils.setEnable(!lock);
                        }
                    }
                })
                .build(detailPlayer);
        detailPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFullScreen = true;
                MyVideoPlayer p = (MyVideoPlayer) detailPlayer.startWindowFullscreen(VideoActivity.this, false, false);
                p.getNextVideo().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemsList.size() <= position + 1) {
                            position = (position + 1) % itemsList.size();
                            isPlay = true;
                            p.getCurrentPlayer().release();
                            p.setUp(itemsList.get(position).getUrl(), true, itemsList.get(position).getTwittertext());
                            p.startPlay();
                            return;
                        }
                        position++;
                        isPlay = true;
                        p.getCurrentPlayer().release();
                        p.setUp(itemsList.get(position).getUrl(), true, itemsList.get(position).getTwittertext());
                        p.startPlay();
                    }
                });
            }
        });
        detailPlayer.getNextVideo().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemsList.size() <= position + 1) {
                    position = (position + 1) % itemsList.size();
                    isPlay = true;
                    detailPlayer.getCurrentPlayer().release();
                    detailPlayer.setUp(itemsList.get(position).getUrl(), true, itemsList.get(position).getTwittertext());
                    detailPlayer.startPlay();
                    return;
                }
                position++;
                isPlay = true;
                detailPlayer.getCurrentPlayer().release();
                detailPlayer.setUp(itemsList.get(position).getUrl(), true, itemsList.get(position).getTwittertext());
                detailPlayer.startPlay();
            }
        });

        detailPlayer.getFullscreenButton().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                isFullScreen=true;
//                orientationUtils.resolveByClick();
//                detailPlayer.startWindowFullscreen(VideoActivity.this,false,true);
//                return false;
                Intent intent = new Intent();
                ArrayList<VideoModel> vm = new ArrayList<>();
                for (Items ii : itemsList) {
                    VideoModel vvv = new VideoModel();
                    vvv.setUrl(ii.getUrl());
                    vm.add(vvv);
                }
                Collections.shuffle(vm);
                intent.putExtra("list", vm);
                intent.putExtra("i", position);
                intent.setClass(VideoActivity.this, tiktok.class);
                startActivity(intent);
                return true;
            }
        });
//
        blank = findViewById(R.id.blank_layout);
        r = findViewById(R.id.r);
        recyclerView = findViewById(R.id.recyclerview);
        layoutManager = new CustomLinearLayoutManager(this);
        layoutManager.setScrollEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        itemsList.clear();

        itemsList = getDataList(url);
        if (itemsList.size() != 0) {
            HaveList = true;
            isScaning = false;
            //loadPic();
            LoadingUtil.Loading_close();
        }
        adapter = new ItemAdapter(itemsList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                position = postion;
                if (!canChange) {
                    isPlay = true;
                    title.setVisibility(View.GONE);
                    canChange = true;
                    detailPlayer.setUp(itemsList.get(postion).getUrl(), true, itemsList.get(postion).getTwittertext());
                    changList();
//                    vv.setVideoPath(srcList.get(postion));
                } else {
                    isPlay = true;
                    detailPlayer.getCurrentPlayer().release();
                    detailPlayer.setUp(itemsList.get(postion).getUrl(), true, itemsList.get(postion).getTwittertext());
                    detailPlayer.startPlay();

                }
            }
        });
        adapter.setOnItemLongClickListener(new ItemAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int postion) {
                new IosAlertDialog(VideoActivity.this).builder()
                        .setTitle("提示")
                        .setMsg("确认删除" + itemsList.get(postion).getText() + "吗？")
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setPositiveButton("确认", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                File f = new File(itemsList.get(postion).getUrl());
                                if (f.exists()) {
                                    f.delete();
                                }
                                Toast.makeText(VideoActivity.this, "文件" + itemsList.get(postion).getText() + "删除成功！", Toast.LENGTH_SHORT).show();
                                itemsList.remove(postion);
                                adapter.update(itemsList);
                                if (itemsList.size() == 0) {
                                    setBlankUI();
                                }

                            }
                        })
                        .show();
            }
        });
        File f = new File(url);
        num = 0;
        if (!f.exists()) {
            f.mkdirs();
            Log.e(TAG, "不存在");

        }
        toScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startScan();
            }
        });
        /*********************
         *
         *
         *
         * 给了存储权限但是读取不到文件
         * 设置build.gradle的targecompileversion
         * 在23下，并且安装后重新改为原来的就好了
         *

         * ******************/
        if (f.list() == null || f.list().length == 0) {
            if (f.list() == null) {
                //getPermission();
                Log.e(TAG, "不存在");
            }
            //Toast.makeText(this, "文件夹下什么文件都没有噢", Toast.LENGTH_SHORT).show();
            LoadingUtil.Loading_close();
            setBlankUI();
            isNull = true;
            scan.setVisibility(View.INVISIBLE);
            toScan.setVisibility(View.VISIBLE);

            scanNum.setVisibility(View.INVISIBLE);
            scanNum.setText(++num + "");
            return;
        }
        isNull = false;
        if(kv_text.count()==0){
            Log.d(TAG, "没有推文存储");
        }
        new Thread(() -> {
            for (final String s : f.list()) {
                if (!s.endsWith(".mp4")) {
                    continue;
                }
                if (!s.substring(s.length() - 4, s.length()).equals(".mp4")) {
                    continue;
                }
                String uu = url + s;
                String text = kv_text.decodeString(s, "");

                final Items i = new Items();
                File file = new File(uu);
                double d = (new BigDecimal(file.length() / (1024 * 1024.0))
                        .setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
                String len = d + "MB";
                BasicFileAttributes attr = null;
                Instant instant = null;
                String time = null;
                if ((s.length() == 22 || s.length() == 21) && s.startsWith("20")) {
                    time = s.substring(0, 4) + "/" + s.substring(4, 6) + "/" + s.substring(6, 8) + " " + s.substring(8, 10) + ":" + s.substring(10, 12);
                } else {
                    try {
                        Path path = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            path = file.toPath();
                            attr = Files.readAttributes(path, BasicFileAttributes.class);
                            instant = attr.creationTime().toInstant();
                        }
                        if (instant != null) {
                            String temp = instant.toString().replace("T", " ").replace("Z", "").replace("-", "/");
                            time = temp.substring(0, temp.length() - 3);
                        } else {
                            long timeee = file.lastModified();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                            time = formatter.format(timeee);
                        }
                    } catch (Exception e) {
                        long timeee = file.lastModified();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        time = formatter.format(timeee);
                        Log.e(TAG, "modifiedTime: " + time + e.getMessage());
                    }
                }
               // Log.d(TAG, "初始扫描的time:"+time);
                i.setSize(len);
                i.setText(s);
                i.setTime(time);
                i.setUrl(uu);
                i.setTwittertext(text);
                if (HaveList) {
                    newItemsList.add(0, i);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scanNum.setText(++num + "");
                        }
                    });
                } else {
                    itemsList.add(0, i);
                    handler.sendEmptyMessage(SEARCH_ONE_VIDEO);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scanNum.setText(++num + "");
                        }
                    });
                }

            }
            if (HaveList) {
                //之前扫描过，存在有list
                if (itemsList.size() != newItemsList.size()) {
                    //list长度不一致，需要更新
                    sort(newItemsList);
                    setDataList(url, newItemsList);
                    handler.sendEmptyMessage(UPDATE_LIST);
                } else {
                    //list长度不一致，不需要更新
                    handler.sendEmptyMessage(NO_UPDATE_LIST);
                }
            } else {
                //刚进入第一次扫描
                //
                sort(itemsList);
                setDataList(url, itemsList);
                handler.sendEmptyMessage(SEARCH_VIDEO);
            }
        }).start();


    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            isFullScreen = false;
            Log.e(TAG, "退出全屏");
            return;
        }
        if (canChange) {
            if (isPlay) {
                detailPlayer.getCurrentPlayer().release();
                isPlay = false;
            }
            reChangeList();
            canChange = false;
            return;
        } else {
            super.onBackPressed();
            //Toast.makeText(this, "222", Toast.LENGTH_SHORT).show();
            finish();
        }
        super.onBackPressed();

    }


    @Override
    protected void onPause() {
        detailPlayer.getCurrentPlayer().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        detailPlayer.getCurrentPlayer().onVideoResume(false);
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            detailPlayer.getCurrentPlayer().release();
        }
        itemsList.clear();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    public void startScan() {
        if(toScan.getVisibility()==View.INVISIBLE){
            Toast.makeText(this, "正在扫描中，请稍后重试...", Toast.LENGTH_SHORT).show();
            return;
        }
        toScan.setVisibility(View.INVISIBLE);
        scanNum.setVisibility(View.VISIBLE);
        scan.setVisibility(View.VISIBLE);
        File f = new File(url);
        num = 0;
        if (!f.exists()) {
            f.mkdirs();
        }
        if (f.list() == null || f.list().length == 0) {
            if (f.list() == null) {
                //getPermission();
                Log.e(TAG, "不存在");
            }
            //Toast.makeText(this, "文件夹下什么文件都没有噢", Toast.LENGTH_SHORT).show();
            LoadingUtil.Loading_close();
            setBlankUI();
            isNull = true;
            scan.setVisibility(View.INVISIBLE);
            toScan.setVisibility(View.VISIBLE);
            scanNum.setVisibility(View.INVISIBLE);
            return;
        }
        isNull = false;
        HaveList=true;
        newItemsList.clear();
        new Thread(() -> {
            long before = System.currentTimeMillis();
            for (final String s : f.list()) {
                if (!s.endsWith(".mp4")) {
                    continue;
                }
                if (!s.substring(s.length() - 4, s.length()).equals(".mp4")) {
                    continue;
                }
                String uu = url + s;
                String text = kv_text.decodeString(s, "");
                final Items i = new Items();
                File file = new File(uu);
                double d = (new BigDecimal(file.length() / (1024 * 1024.0))
                        .setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
                String len = d + "MB";
                BasicFileAttributes attr = null;
                Instant instant = null;
                String time = null;
                if ((s.length() == 22 || s.length() == 21) && s.startsWith("20")) {
                    time = s.substring(0, 4) + "/" + s.substring(4, 6) + "/" + s.substring(6, 8) + " " + s.substring(8, 10) + ":" + s.substring(10, 12);
                } else {
                    try {
                        Path path = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            path = file.toPath();
                            attr = Files.readAttributes(path, BasicFileAttributes.class);
                            instant = attr.creationTime().toInstant();
                        }
                        if (instant != null) {
                            String temp = instant.toString().replace("T", " ").replace("Z", "").replace("-", "/");
                            time = temp.substring(0, temp.length() - 3);
                        } else {
                            long timeee = file.lastModified();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                            time = formatter.format(timeee);
                        }
                    } catch (Exception e) {
                        long timeee = file.lastModified();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        time = formatter.format(timeee);
                        Log.e(TAG, "modifiedTime: " + time + e.getMessage());
                    }
                }
               // Log.d(TAG, "后扫的time: " + time);
                i.setSize(len);
                i.setText(s);
                i.setTime(time);
                i.setUrl(uu);
                i.setTwittertext(text);
                newItemsList.add(0, i);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scanNum.setText(++num + "");
                    }
                });
            }
            if (itemsList.size() != newItemsList.size()) {
                Log.d(TAG, itemsList.toString());
                sort(newItemsList);
                setDataList(url, newItemsList);
                handler.sendEmptyMessage(UPDATE_LIST);
            } else {
                if(twitterIsEmpty){
                    sort(newItemsList);
                    setDataList(url, newItemsList);
                    itemsList = newItemsList;
                    runOnUiThread(()->{
                        adapter.update(itemsList);
                    });
                    loadPic();
                    //惊呆了老铁铁，这是什么操作，从来没见过，真是让我开了眼。
                    twitterIsEmpty=false;
                }
                handler.sendEmptyMessage(NO_UPDATE_LIST);
            }
            long after = System.currentTimeMillis();
            Log.d(TAG, "time:"+(after-before));
        }).start();
    }

    public void reChangeList() {
        showStatusBar();
        final int screenHeight = getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如：800p）
        //int height =vv.getMeasuredHeight();
        // final int i=getResources().getDimensionPixelSize(R.dimen.dp_300);
        AnimatorSet animatorSet = new AnimatorSet();
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = screenHeight;
        recyclerView.setLayoutParams(params);
        title.setVisibility(View.VISIBLE);

        ObjectAnimator animator4 = ObjectAnimator.ofFloat(recyclerView, "translationY", 0);
        animatorSet.play(animator4);
        animatorSet.setDuration(500).start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                detailPlayer.getCurrentPlayer().onVideoPause();
                isPause = true;

                if (isPlay) {
                    isPlay = false;
                    detailPlayer.getCurrentPlayer().release();
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    public void setDataList(String tag, ArrayList<Items> datalist) {
        HaveList=true;
        if (null == datalist || datalist.size() <= 0)
            return;

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        kv.encode(tag, strJson);
    }

    /**
     * 获取List
     *
     * @param tag
     * @return
     */
    public ArrayList<Items> getDataList(String tag) {
        ArrayList<Items> datalist = new ArrayList<Items>();
        //String strJson = sp.getString(tag, null);

        String strJson = kv.decodeString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<ArrayList<Items>>() {
        }.getType());
        return datalist;

    }

    public void setBlankUI() {
        blank.setVisibility(View.VISIBLE);
    }

    public void changList() {

        //int height =vv.getMeasuredHeight();
        final int i = getResources().getDimensionPixelSize(R.dimen.dp_400);
        AnimatorSet animatorSet = new AnimatorSet();
        //ViewWrapper wrapper = new ViewWrapper(recyclerView);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(recyclerView, "translationY", getResources().getDimensionPixelSize(R.dimen.dp_400));
        animatorSet.play(animator4);
        animatorSet.setDuration(500).start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hideStatusBar();
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                int screenHeight = getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如：800p）
                params.height = screenHeight - i + getResources().getDimensionPixelSize(R.dimen.dp_40);
                recyclerView.setLayoutParams(params);
                detailPlayer.startPlay();
                //vv.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            detailPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }
    public Bitmap getBitmap(String path) throws IOException {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return bitmap;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getURLimage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public void sort(ArrayList<Items> stus) {
        isScaning = false;
        Collections.sort(stus, (o1, o2) -> {
            // 升序
            //return o1.getAge()-o2.getAge();
            if(o2.getTime()==null||o1.getTime()==null){
                Log.d(TAG, "视频时间null");
                return 1;
            }
            return o2.getTime().compareTo(o1.getTime());
            // 降序
            // return o2.getAge()-o1.getAge();
            // return o2.getAge().compareTo(o1.getAge());
        });

    }

    public void deSort(ArrayList<Items> stus) {
        isScaning = false;
        Collections.sort(stus, new Comparator<Items>() {

            @Override
            public int compare(Items o1, Items o2) {
                if(o2.getTime()==null||o1.getTime()==null){
                    Log.d(TAG, "视频时间null");
                    return 1;
                }
                return o1.getTime().compareTo(o2.getTime());
            }
        });

    }

    public void sortByLarge(ArrayList<Items> stus) {
        isScaning = false;
        Collections.sort(stus, new Comparator<Items>() {

            @Override
            public int compare(Items o1, Items o2) {
                if(o2.getVideo_len()==null||o1.getVideo_len()==null){
                    Log.d(TAG, "视频时长报Null");
                    return 1;
                }
                return o2.getVideo_len().compareTo(o1.getVideo_len());
            }
        });

    }

    public void sortByComment(ArrayList<Items> stus) {
        isScaning = false;
        Collections.sort(stus, new Comparator<Items>() {
            @Override
            public int compare(Items o1, Items o2) {
                if(o2.getTwittertext()==null||o1.getTwittertext()==null){

                    return 1;
                }
                return o2.getTwittertext().length() - o1.getTwittertext().length();
            }
        });

    }

    public void deSortByComment(ArrayList<Items> stus) {
        isScaning = false;
        Collections.sort(stus, (o1, o2) -> {
            if(o2.getTwittertext()==null||o1.getTwittertext()==null){
                Log.d(TAG, "推特文案长度报Null");
                return 1;
            }
            return o1.getTwittertext().length() - o2.getTwittertext().length();
        });

    }

    public void desortByLarge(ArrayList<Items> stus) {
        isScaning = false;
        Collections.sort(stus, (o1, o2) -> {
            if(o2.getTwittertext()==null||o1.getTwittertext()==null){
                Log.d(TAG, "视频长度报Null");
                return 1;
            }
            return o1.getVideo_len().compareTo(o2.getVideo_len());
        });

    }

    public void sortSrcList(ArrayList<String> stus) {
        Collections.sort(stus, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {

                return o2.compareTo(o1);
            }
        });
    }

    void getPermission() {
        int permissionCheck1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    124);
        }
    }

    private void hideStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    //全屏并且状态栏透明显示
    private void showStatusBar() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }
    private void loadTwitterText(){
        new Thread(()->{
            BmobQuery<TwitterText> query = new BmobQuery<>();
            query.order("createdAt")
                    .findObjects(new FindListener<TwitterText>() {
                        @Override
                        public void done(List<TwitterText> object, BmobException e) {
                            if (e == null) {
                                //Log.d(TAG, "下载的文案"+object.get(0).getFilename()+object.get(0).getText()+object.get(0));
                                for(TwitterText tt:object){
                                    kv_text.encode(tt.getFilename(),tt.getText());
                                }
                                twitterIsEmpty=true;
                                startScan();
                            } else {

                            }
                        }
                    });

        }).start();
    }
    private void loadPic() {
        new Thread(() -> {
            isScaning = true;
            for (int i = 0; i < itemsList.size(); i++) {
                if (isScaning) {
                    if (i < itemsList.size()) {
                        if(itemsList.get(i).getVideo_len()==null||itemsList.get(i).getVideo_len().isEmpty()){
                            itemsList.get(i).setVideo_len(loadVideoLen(itemsList.get(i).getUrl()));
                            Message msg = new Message();
                            msg.what = SCANING_ONE_PIC;
                            msg.obj = i;
                            if (isScaning) {
                                handler.sendMessage(msg);
                            }
                        }
                    } else {
                        return;
                    }
                } else {
                    return;
                }
                if(i==itemsList.size()-1){
                    setDataList(url, itemsList);
                }
            }
        }).start();
    }

    //    public void addBitmapToMemoryCache(String key,DiskLruCache mDiskCache, Bitmap bitmap) throws IOException {
//
////        if (getBitmapFromMemCache(key) == null) {
////            mMemoryCache.put(key, bitmap);
////        }
//        DiskLruCache.Editor editor = mDiskCache.edit(key);
//        OutputStream outputStream = editor.newOutputStream(0);// 0表示第一个缓存文件，不能超过valueCount
//        outputStream.write(bitmap.);
//        outputStream.close();
//        editor.commit();
//        mDiskCache.flush();
//
//
//    }
//public void getDiskCache(String key) throws IOException {
//    File directory = VideoActivity.this.getCacheDir();
//    DiskLruCache diskLruCache = DiskLruCache.open(directory, 1, 1, 1024 * 1024 * 10);
//    String value = diskLruCache.get(key).getString(0);
//    diskLruCache.close();
//}
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");//把uri编译为MD5,防止网址有非法字符
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private synchronized Bitmap getCache(String key) {
//        Log.e(TAG, "get" );
        key = hashKeyForDisk(key);
        Log.e(TAG, "imageKey: " + key);
        try {
                DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
                if (snapshot != null) {
                    InputStream in = snapshot.getInputStream(0);
                    return BitmapFactory.decodeStream(in);
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void addDiskCache(String key, Bitmap bitmap) throws IOException {
        //Log.e(TAG, "put" );
        key = hashKeyForDisk(key);
        DiskLruCache.Editor editor = mDiskCache.edit(key);
        // index与valueCount对应，分别为0,1,2...valueCount-1
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } catch (Exception e) {
            Log.e(TAG, "addDiskCache: " + e.getMessage());
            baos.close();
            return;
        }
        editor.newOutputStream(0).write(baos.toByteArray());
        editor.commit();
        baos.close();
        //mDiskCache.close();
    }


    public synchronized void loadBitmap(String imageKey, int i) throws IOException {
        Bitmap bitmap = getCache(imageKey);
        if (bitmap != null) {
            Log.e(TAG, "读取");
            if (i < itemsList.size()) {

                itemsList.get(i).setSrc(bitmap);
            } else {
                return;
            }
        } else {
            Log.e(TAG, "生成");
            try {
                bitmap = ThumbnailUtils.createVideoThumbnail(itemsList.get(i).getUrl(), MediaStore.Images.Thumbnails.MINI_KIND);
            } catch (Exception e) {
                Log.e(TAG, "loadBitmap: " + e.getMessage());
                return;
            }

            if (i < itemsList.size()) {
                itemsList.get(i).setSrc(bitmap);
            } else {
                return;
            }
            addDiskCache(itemsList.get(i).getUrl(), bitmap);
        }
    }

    //    public Bitmap getBitmapFromMemCache(String key) {
//        return mMemoryCache.get(key);
//    }

    private String loadVideoLen(String uu){
        String tt="";
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(uu);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
        }
        long timee = mediaPlayer.getDuration() / 1000;//获得了视频的时长（以毫秒为单位）
        mediaPlayer.release();
        mediaPlayer = null;
        if (timee / 60 != 0) {
            if (timee % 60 < 10) {
                tt = timee / 60 + ":0" + timee % 60;
            } else {
                tt = timee / 60 + ":" + timee % 60;
            }
        } else {
            if (timee % 60 < 10) {
                tt = "00:0" + timee % 60;
            } else {
                tt = "00:" + timee % 60;
            }
        }
        return tt;
    }
    private synchronized void loadPicAfterSort() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                isScaning = true;
                for (int i = 0; i < itemsList.size(); i++) {
                    if (isScaning) {
                        if (itemsList.get(i).getSrc() == null) {
                            if (isScaning) {
                                if (i < itemsList.size()) {
                                    if(itemsList.get(i).getVideo_len()==null||itemsList.get(i).getVideo_len()=="") {
                                        itemsList.get(i).setVideo_len(loadVideoLen(itemsList.get(i).getUrl()));
                                    }
                                    Message msg = new Message();
                                    msg.what = SCANING_ONE_PIC;
                                    msg.obj = i;
                                    handler.sendMessage(msg);
                                } else {
                                    return;
                                }

                            } else {
                                return;
                            }
                        }
                    } else {
                        return;
                    }
                }
            }
        }).start();
    }

    public void setStatusBarColor() {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}