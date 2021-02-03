package com.ycs.servicetest;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
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
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;
//
//import com.geccocrawler.gecco.annotation.Gecco;
//import com.geccocrawler.gecco.annotation.RequestParameter;
//import com.geccocrawler.gecco.annotation.Text;

import com.jakewharton.disklrucache.DiskLruCache;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Wrapper;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VideoActivity extends AppCompatActivity {
//    private WebView webView;
//    private TextView tv;
//    private TextView tvWeb;
    private ItemAdapter adapter;
    private Boolean isScaning=false;
    private Boolean canChange=false;
    private CustomLinearLayoutManager layoutManager ;
    private RecyclerView recyclerView;
    private ArrayList<Items> itemsList=new ArrayList<>();
    //private ArrayList<String> srcList=new ArrayList<>();
    //private VideoView vv;
    private MyVideoPlayer detailPlayer;
    private boolean isPlay=false;
    private boolean isPause;
    private RelativeLayout blank;
    private OrientationUtils orientationUtils;
    //private String url=Environment.getExternalStorageDirectory() +"/123/";
    //private String url=Environment.getExternalStorageDirectory() +"/DCIM/Camera/";
    private String url=Environment.getExternalStorageDirectory() +"/.savedPic/";
    private boolean isFullScreen=false;
    private ImageView iv;
    private DiskLruCache mDiskCache;
    //private ImageView blank;
    public static final int SEARCH_VIDEO=1;
    public static final int SEARCH_ONE_VIDEO=2;
    public static final int SCANING_ONE_PIC=3;
    public static final int AFTER_SORT_SCAN=4;
    public static final int UPDATE_ALL=5;
    private RelativeLayout title;
//    private LruCache<String, Bitmap> mMemoryCache;
    private RelativeLayout sortImage;
    private SharedPreferences sp;
    private int position=0;
    int i[]={0,0,0};
    static final String TAG="yyy";
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEARCH_VIDEO:
                    adapter.update(itemsList);
                    Toast.makeText(VideoActivity.this, "共找到"+itemsList.size()+"个视频", Toast.LENGTH_SHORT).show();
                    LoadingUtil.Loading_close();
                    loadPic();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        getSupportActionBar().hide();
        setStatusBarColor();
        SharedPreferences spp=getSharedPreferences("url",Context.MODE_PRIVATE);
        if(!spp.getString("url","").equals("")){
            url=spp.getString("url","");
        }
//        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
//        // Use 1/8th of the available memory for this memory cache.
//        final int cacheSize = maxMemory / 8;
        File ff=new File(url);
        if (!ff.exists()) {
            // 若文件夹不存在，建立文件夹
            ff.mkdirs();
        }
        try {
            mDiskCache = DiskLruCache.open(ff, 1, 1,   1024 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
//            @Override
//            protected int sizeOf(String key, Bitmap bitmap) {
//                return bitmap.getByteCount() / 1024;
//            }
//        };
        LoadingUtil.Loading_show(this);
        iv=findViewById(R.id.back);
        title=findViewById(R.id.title);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        sortImage=findViewById(R.id.sort);
        sortImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(VideoActivity.this, "点了", Toast.LENGTH_SHORT).show();
                new XPopup.Builder(VideoActivity.this)
                        .atView(sortImage)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                        .asAttachList(new String[]{"按下载时间排序", "按视频时长排序","按描述排序"},
                                new int[]{R.mipmap.downloadtime,R.mipmap.video,R.mipmap.miaoshu},
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {
                                        if(position==0){
                                            isScaning=false;
                                            if(i[position]%2==0){
                                                deSort(itemsList);
                                            }else{
                                                sort(itemsList);
                                            }
                                            i[position]++;
                                            adapter.update(itemsList);
                                            handler.sendEmptyMessage(AFTER_SORT_SCAN);
                                        }else if(position==1){
                                            isScaning=false;
                                            if(i[position]%2==0){
                                                desortByLarge(itemsList);
                                            }else{
                                                sortByLarge(itemsList);
                                            }
                                            i[position]++;
                                            adapter.update(itemsList);
                                            handler.sendEmptyMessage(AFTER_SORT_SCAN);
                                        }else if(position==2){
                                            isScaning=false;
                                            if(i[position]%2==0){
                                                deSortByComment(itemsList);
                                            }else{
                                                sortByComment(itemsList);
                                            }
                                            i[position]++;
                                            //sortByComment(itemsList);
                                            adapter.update(itemsList);
                                            handler.sendEmptyMessage(AFTER_SORT_SCAN);
                                        }

                                    }
                                })
                        .show();


            }
        });

//                        .asBottomList("选择排序规则", new String[]{"按下载时间排序", "按视频时长排序"},
//                                new OnSelectListener() {
//                                    @Override
//                                    public void onSelect(int position, String text) {
//                                        Toast.makeText(VideoActivity.this, "我点了"+text+"这个选项！", Toast.LENGTH_SHORT).show();
//                                    }
//                                })

        sp=getSharedPreferences("text", Context.MODE_PRIVATE);
        detailPlayer =findViewById(R.id.detail_player);
        detailPlayer.getTitleTextView().setVisibility(View.GONE);
        detailPlayer.getBackButton().setVisibility(View.GONE);
        orientationUtils=new OrientationUtils(this,detailPlayer);
        orientationUtils.setEnable(false);
        GSYVideoOptionBuilder gsyVideoOption=new GSYVideoOptionBuilder();
        gsyVideoOption
                .setIsTouchWiget(true)
                .setRotateViewAuto(true)
                .setLockLand(false)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                //.setUrl(url)
                .setOnlyRotateLand(true)
                //.setRotateWithSystem(true)
                .setCacheWithPlay(true)
                .setVideoTitle("这里是一个竖直方向的视频")
                .setSeekRatio(1)
                .setVideoAllCallBack(new GSYSampleCallBack(){
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        orientationUtils.setEnable(detailPlayer.isRotateWithSystem());
                        isPlay=true;
                    }

                    @Override
                    public void onClickStartThumb(String url, Object... objects) {
                        super.onClickStartThumb(url, objects);
                        //hideStatusBar();
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {

                        super.onAutoComplete(url, objects);
                        isPlay=true;
                        detailPlayer.restart();
                    }

                    @Override
                    public void onPlayError(String url, Object... objects) {
                        super.onPlayError(url, objects);
                        isPlay=false;
                        Toast.makeText(VideoActivity.this, "播放错误", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        if(orientationUtils!=null){
                            orientationUtils.backToProtVideo();
                        }
                        isFullScreen=false;
                    }
                })
                .setLockClickListener(new LockClickListener() {
                    @Override
                    public void onClick(View view, boolean lock) {
                        if(orientationUtils!=null){
                            orientationUtils.setEnable(!lock);
                        }
                    }
                })
                .build(detailPlayer);
        detailPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFullScreen=true;
                 //orientationUtils.resolveByClick();

                detailPlayer.startWindowFullscreen(VideoActivity.this,false,false);
//                detailPlayer.getTitleTextView().setVisibility(View.GONE);
//                detailPlayer.getBackButton().setVisibility(View.GONE);
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
                ArrayList<VideoModel> vm=new ArrayList<>();
                for(Items ii:itemsList){
                    VideoModel vvv=new VideoModel();
                    vvv.setUrl(ii.getUrl());
                    vm.add(vvv);
                }
                intent.putExtra("list", vm);
                intent.putExtra("i",position);
                intent.setClass(VideoActivity.this, tiktok.class);
                startActivity(intent);
                return true;
            }
        });
       //vv=findViewById(R.id.videoview);
        blank=findViewById(R.id.blank_layout);
        //MediaController mediaController = new MediaController(this);
        //vv.setMediaController(mediaController);
        //detailPlayer.setVisibility(View.INVISIBLE);
        r=findViewById(R.id.r);
        //mediaController.setMediaPlayer(vv);
        recyclerView=findViewById(R.id.recyclerview);
        layoutManager = new CustomLinearLayoutManager(this);
        layoutManager.setScrollEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        itemsList.clear();

        adapter=new ItemAdapter(itemsList);
        recyclerView.setAdapter(adapter);
        File f=new File(url);
        if(!f.exists()){
            f.mkdirs();
            Log.e(TAG, "不存在" );

        }
        if(f.list()==null||f.list().length==0){
            if(f.list()==null){
                //getPermission();
                Log.e(TAG, "不存在" );
            }
            //Toast.makeText(this, "文件夹下什么文件都没有噢", Toast.LENGTH_SHORT).show();
            LoadingUtil.Loading_close();
            setBlankUI();
            return;
        }
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                for(final String s:f.list()){
                    if(!s.endsWith(".mp4")){
                        continue;
                    }
                    if(!s.substring(s.length()-4,s.length()).equals(".mp4")){
                        continue;
                    }
                    String uu=url+s;
                    String text=sp.getString(s,"");
//                    String t=text.replace("\n","  ");
//                    if(!t.equals(text)){
//                        SharedPreferences.Editor e=sp.edit();
//                        e.putString(s,t);
//                        e.commit();
//                    }
                    Log.e("yyy",s);
                    final Items i=new Items();
                    File file=new File(uu);
                    double d=(new BigDecimal(file.length() / (1024*1024.0))
                            .setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
                    String len=d+"MB";
                    //
                    BasicFileAttributes attr = null;
                    Instant instant=null;
                    String time=null;
                    if((s.length()==22||s.length()==21)&&s.startsWith("20")){
                        time=s.substring(0,4)+"/"+s.substring(4,6)+"/"+s.substring(6,8)+" "+s.substring(8,10)+":"+s.substring(10,12);
                    }else{
                        try {
                            Path path =  file.toPath();
                            attr = Files.readAttributes(path, BasicFileAttributes.class);
                            instant= attr.creationTime().toInstant();
                            Log.e(TAG, "createTime: "+instant );
                            String temp=instant.toString().replace("T"," ").replace("Z","").replace("-","/");
                            time=temp.substring(0,temp.length()-3);
                            //Log.e(TAG, "createTime: "+time );
                        } catch (IOException e) {
                            long timeee=file.lastModified();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                            time=formatter.format(timeee);
                            Log.e(TAG, "modifiedTime: "+time );
                        }
                    }
                    // 创建时间
                    //String time=s.substring(0,4)+"."+s.substring(4,6)+"."+s.substring(6,8)+" "+s.substring(8,10)+":"+s.substring(10,12);
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
                    long timee =  mediaPlayer.getDuration()/1000;//获得了视频的时长（以毫秒为单位）
                    mediaPlayer.release();
                    mediaPlayer=null;
                    String tt;
                    if(timee/60!=0){
                        if(timee%60<10){
                            tt=timee/60+":0"+timee%60;
                        }else{
                            tt=timee/60+":"+timee%60;
                        }
                    }else{
                        if(timee%60<10){
                            tt="00:0"+timee%60;
                        }else{
                            tt="00:"+timee%60;
                        }

                    }
                    i.setVideo_len(tt);
                    i.setSize(len);
                    i.setText(s);
                    i.setTime(time);
                    i.setUrl(uu);
                    i.setTwittertext(text);
                    itemsList.add(0,i);
                    handler.sendEmptyMessage(SEARCH_ONE_VIDEO);
                }
                sort(itemsList);
                handler.sendEmptyMessage(SEARCH_VIDEO);
            }
        }).start();
        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                position=postion;
                if(!canChange){
                    isPlay=true;
                    title.setVisibility(View.GONE);
                    canChange=true;
                    detailPlayer.setUp(itemsList.get(postion).getUrl(),true,itemsList.get(postion).getTwittertext());
                    changList();
//                    vv.setVideoPath(srcList.get(postion));
                }else{
                    isPlay=true;
                    detailPlayer.getCurrentPlayer().release();
                    detailPlayer.setUp(itemsList.get(postion).getUrl(),true,itemsList.get(postion).getTwittertext());
                    detailPlayer.startPlay();

                }
            }
        });
        adapter.setOnItemLongClickListener(new ItemAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int postion) {
                new IosAlertDialog(VideoActivity.this).builder()
                        .setTitle("提示")
                        .setMsg("确认删除"+itemsList.get(postion).getText()+"吗？")
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setPositiveButton("确认", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                File f=new File(itemsList.get(postion).getUrl());
                                if(f.exists()){
                                    f.delete();
                                }
                                Toast.makeText(VideoActivity.this, "文件"+itemsList.get(postion).getText()+"删除成功！", Toast.LENGTH_SHORT).show();
                                itemsList.remove(postion);
                                adapter.update(itemsList);
                                if(itemsList.size()==0){
                                    setBlankUI();
                                }

                            }
                        })
                        .show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            isFullScreen=false;
            Log.e(TAG, "退出全屏" );
            return;
        }
        if(canChange){
            if (isPlay) {
                detailPlayer.getCurrentPlayer().release();
                isPlay=false;
            }
            reChangeList();
            canChange=false;
            return;
        }else{
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
    public void reChangeList(){
        showStatusBar();
        final int screenHeight = getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如：800p）
        //int height =vv.getMeasuredHeight();
       // final int i=getResources().getDimensionPixelSize(R.dimen.dp_300);
        AnimatorSet animatorSet = new AnimatorSet();
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height=screenHeight;
        recyclerView.setLayoutParams(params);
        title.setVisibility(View.VISIBLE);
       // ViewWrapper wrapper = new ViewWrapper(recyclerView);
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
                    isPlay=false;
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
    public void setBlankUI(){
        //detailPlayer.setVisibility(View.GONE);
       // recyclerView.setVisibility(View.INVISIBLE);
        blank.setVisibility(View.VISIBLE);
    }
    public void changList(){

        //int height =vv.getMeasuredHeight();
        final int i=getResources().getDimensionPixelSize(R.dimen.dp_400);
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
                params.height=screenHeight-i+getResources().getDimensionPixelSize(R.dimen.dp_40);;
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
    private static class ViewWrapper {
        private View mTarget;

        public ViewWrapper(View target) {
            mTarget = target;
        }

        public int getWidth() {
            return mTarget.getLayoutParams().width;
        }

        public void setWidth(int width) {
            mTarget.getLayoutParams().width = width;
            mTarget.requestLayout();
        }
        public int getHeight() {
            return mTarget.getLayoutParams().width;
        }

        public void setHeight(int height) {
            mTarget.getLayoutParams().height = height;
            mTarget.requestLayout();
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

//    public void downloadPicture(final String uurl){
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                URL url = null;
//                HttpURLConnection con = null;
//                try {
//
//                    url = new URL(uurl);
//                    con = (HttpURLConnection) url.openConnection();
//                    con.setRequestMethod("GET");
//                    con.setReadTimeout(5000);
//                    con.setDoInput(true);
//                    double length=con.getContentLength();
//                    String filename = Environment.getExternalStorageDirectory() +"/.savedPic/";
//                    File file = new File(filename);
//                    if(!file.exists()){
//                        file.mkdirs();
//                    }
//                    File f = new File(file,String.valueOf(System.currentTimeMillis()+".jpg"));
//                    final String fname=f.getName();
//                    Uri uri = null;
//                    if(Build.VERSION.SDK_INT>= 24){
//                        uri = FileProvider.getUriForFile(VideoActivity.this, "com.ycs.servicetest.provider", f);
//                    }else{
//                        uri= Uri.fromFile(f);
//                    }
//                    OutputStream fos= (OutputStream) getContentResolver().openOutputStream(uri);
////                    FileOutputStream fos = new FileOutputStream(file);
//                    InputStream in = con.getInputStream();
//                    byte ch[] = new byte[2 * 1024];
//                    int len=0;
//                    long haswrite=0L;
//                    int percent=0;
//                    if (fos!= null){
//                        while ((len = in.read(ch)) != -1){
//                            fos.write(ch,0,len);
//                            haswrite += len;
//                            //BidDecimal精确数
//                            double d = (new BigDecimal(haswrite / length)
//                                    .setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
//                            percent = (int) (d * 100);
//                            Log.e("yyy", "进度"+percent);
//                            MainService.updateProgress(VideoActivity.this,percent);
//
//                        }
////                        if(percent==100){
////                            Looper.prepare();
////                            mhandler.post(new Runnable() {
////                                @Override
////                                public void run() {
////                                    Toast.makeText(VideoActivity.this, fname+"下载成功", Toast.LENGTH_SHORT).show();
////                                }
////                            });
////
////                        }
//                        in.close();
//                        fos.close();
//
//                    }else{
//                        Log.e("yyy", "fou为空" );
//                    }
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                    Log.e("yyy","失败"+e.getMessage());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.e("yyy","失败"+e.getMessage());
//                }
//            }
//        }).start();
//    }
    public void sort(ArrayList<Items> stus){
        isScaning=false;
        Collections.sort(stus, new Comparator<Items>() {

            @Override
            public int compare(Items o1, Items o2) {
                // 升序
                //return o1.getAge()-o2.getAge();
                return o2.getTime().compareTo(o1.getTime());
                // 降序
                // return o2.getAge()-o1.getAge();
                // return o2.getAge().compareTo(o1.getAge());
            }
        });

    }

    public void deSort(ArrayList<Items> stus){
        isScaning=false;
        Collections.sort(stus, new Comparator<Items>() {

            @Override
            public int compare(Items o1, Items o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        });

    }
    public void sortByLarge(ArrayList<Items> stus){
        isScaning=false;
        Collections.sort(stus, new Comparator<Items>() {

            @Override
            public int compare(Items o1, Items o2) {
                return o2.getVideo_len().compareTo(o1.getVideo_len());
            }
        });

    }
    public void sortByComment(ArrayList<Items> stus){
        isScaning=false;
        Collections.sort(stus, new Comparator<Items>() {
            @Override
            public int compare(Items o1, Items o2) {
                return  o2.getTwittertext().length()-o1.getTwittertext().length();
            }
        });

    }
    public void deSortByComment(ArrayList<Items> stus){
        isScaning=false;
        Collections.sort(stus, new Comparator<Items>() {

            @Override
            public int compare(Items o1, Items o2) {
                return o1.getTwittertext().length()-o2.getTwittertext().length();
            }
        });

    }
    public void desortByLarge(ArrayList<Items> stus){
        isScaning=false;
        Collections.sort(stus, new Comparator<Items>() {

            @Override
            public int compare(Items o1, Items o2) {
                return o1.getVideo_len().compareTo(o2.getVideo_len());
            }
        });

    }
    public void sortSrcList(ArrayList<String> stus){
        Collections.sort(stus, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });
    }
    void getPermission()
    {
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
    private void loadPic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                isScaning = true;
                for (int i = 0; i < itemsList.size(); i++) {
                    if (isScaning) {
                        if (i < itemsList.size()) {
                            try {
                                loadBitmap(itemsList.get(i).getUrl(), i);
                            } catch (IOException e) {
                                e.printStackTrace();
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
        key=hashKeyForDisk(key);
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
        key=hashKeyForDisk(key);
        DiskLruCache.Editor editor = mDiskCache.edit(key);
        // index与valueCount对应，分别为0,1,2...valueCount-1
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        editor.newOutputStream(0).write( baos.toByteArray());
        editor.commit();
        baos.close();
        //mDiskCache.close();
}

    public synchronized void loadBitmap(String imageKey, int i) throws IOException {

//        Bitmap bitmap = getBitmapFromMemCache(imageKey);
        Bitmap bitmap = getCache(imageKey);
        if (bitmap != null) {
            Log.e(TAG, "读取" );
            if (i < itemsList.size()) {
                itemsList.get(i).setSrc(bitmap);
            }else{
                return;
            }
        } else {
            Log.e(TAG, "生成" );
            bitmap = ThumbnailUtils.createVideoThumbnail(itemsList.get(i).getUrl(), MediaStore.Images.Thumbnails.MINI_KIND);
            if (i < itemsList.size()) {
                itemsList.get(i).setSrc(bitmap);
            }else{
                return;
            }
            addDiskCache(itemsList.get(i).getUrl(),bitmap);
            //addBitmapToMemoryCache(itemsList.get(i).getUrl(),bitmap);
        }
    }
//    public Bitmap getBitmapFromMemCache(String key) {
//        return mMemoryCache.get(key);
//    }
    private synchronized void loadPicAfterSort(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                isScaning=true;
                for(int i = 0; i < itemsList.size(); i++){
                    if(isScaning){
                        if(itemsList.get(i).getSrc()==null){
                            if(isScaning){
                                if(i<itemsList.size()){
                                    try {
                                        loadBitmap(itemsList.get(i).getUrl(),i);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Message msg=new Message();
                                    msg.what=SCANING_ONE_PIC;
                                    msg.obj=i;
                                    handler.sendMessage(msg);
//                                    handler.sendEmptyMessage(UPDATE_ALL);
                                }else{
                                    return;
                                }

                            } else{
                                return;
                            }
                        }
                    }else{
                        return;
                    }
                }
            }
        }).start();
    }
    public void setStatusBarColor(){
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}