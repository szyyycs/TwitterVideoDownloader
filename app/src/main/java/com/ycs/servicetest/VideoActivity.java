package com.ycs.servicetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;
//
//import com.geccocrawler.gecco.annotation.Gecco;
//import com.geccocrawler.gecco.annotation.RequestParameter;
//import com.geccocrawler.gecco.annotation.Text;

import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

    private Boolean canChange=false;
    private CustomLinearLayoutManager layoutManager ;
    private RecyclerView recyclerView;
    private ArrayList<Items> itemsList=new ArrayList<>();
    private ArrayList<String> srcList=new ArrayList<>();
    private VideoView vv;
    private MyVideoPlayer detailPlayer;
    private boolean isPlay;
    private boolean isPause;
    private OrientationUtils orientationUtils;
    private String url;

    private ImageView iv;
    private ImageView blank;
    public static final int SEARCH_VIDEO=1;
    private RelativeLayout title;
    static final String TAG="yangchaosheng";
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEARCH_VIDEO:
                    sort(itemsList);
                    sortSrcList(srcList);
                    adapter.update(itemsList);
                    LoadingUtil.Loading_close();
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
        LoadingUtil.Loading_show(this);
        iv=findViewById(R.id.back);
        title=findViewById(R.id.title);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        detailPlayer = (MyVideoPlayer) findViewById(R.id.detail_player);
       //vv=findViewById(R.id.videoview);
        blank=findViewById(R.id.blank);
        //MediaController mediaController = new MediaController(this);
        //vv.setMediaController(mediaController);
        //detailPlayer.setVisibility(View.INVISIBLE);
        detailPlayer.getTitleTextView().setVisibility(View.GONE);
        detailPlayer.getBackButton().setVisibility(View.GONE);
        r=findViewById(R.id.r);
        //mediaController.setMediaPlayer(vv);
        recyclerView=findViewById(R.id.recyclerview);
        layoutManager = new CustomLinearLayoutManager(this);
        layoutManager.setScrollEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new ItemAdapter(itemsList);
        recyclerView.setAdapter(adapter);
        final File f=new File(Environment.getExternalStorageDirectory() +"/savedPic/");
        if(f.list()==null){
            Toast.makeText(this, "文件夹下什么文件都没有噢", Toast.LENGTH_SHORT).show();
            LoadingUtil.Loading_close();
            blank.setVisibility(View.VISIBLE);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(final String s:f.list()){
                    // Log.e("www",s.substring(s.length()-4,s.length()));
                    if(!s.substring(s.length()-4,s.length()).equals(".mp4")){
                        continue;
                    }

                    final Items i=new Items();
                    File file=new File(Environment.getExternalStorageDirectory() +"/savedPic/",s);
                    double d=(new BigDecimal(file.length() / (1024*1024.0))
                            .setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
                    String len=d+"MB";
                    String time=s.substring(0,4)+"."+s.substring(4,6)+"."+s.substring(6,8)+" "+s.substring(8,10)+":"+s.substring(10,12);
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(file.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    long timee = mediaPlayer.getDuration()/1000;//获得了视频的时长（以毫秒为单位）
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
                    srcList.add(Environment.getExternalStorageDirectory() +"/savedPic/"+s);
                    Bitmap b = ThumbnailUtils.createVideoThumbnail(Environment.getExternalStorageDirectory() +"/savedPic/"+s
                            , MediaStore.Images.Thumbnails.MINI_KIND);
                    //Bitmap b= WebUtil.createVideoThumbnail(Environment.getExternalStorageDirectory() +"/savedPic/"+s);
                    i.setSrc(b);
                    itemsList.add(i);
                }
                handler.sendEmptyMessage(SEARCH_VIDEO);
            }
        }).start();

//        sort(itemsList);
//        sortSrcList(srcList);
        //sort(srcList);
//        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                if(canChange){
//                    reChangeList();
//                    canChange=false;
//                }
//
//                //Toast.makeText(VideoActivity.this, "变回来啦", Toast.LENGTH_SHORT).show();
//            }
//        });
        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                if(!canChange){
                    changList();
                    title.setVisibility(View.GONE);
                    canChange=true;

                    vv.setVideoPath(srcList.get(postion));
                }else{
                    vv.setVideoPath(srcList.get(postion));
                }
            }
        });
        adapter.setOnItemLongClickListener(new ItemAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int postion) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        if(canChange){
            //Toast.makeText(this, "111", Toast.LENGTH_SHORT).show();

            reChangeList();
            canChange=false;
            return;
        }else{
            super.onBackPressed();
            //Toast.makeText(this, "222", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        vv.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        vv.resume();
    }

    public void reChangeList(){
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
                vv.pause();
               // vv.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    public void changList(){
        final int screenHeight = getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如：800p）
        //int height =vv.getMeasuredHeight();
        final int i=getResources().getDimensionPixelSize(R.dimen.dp_300);
        AnimatorSet animatorSet = new AnimatorSet();
        //ViewWrapper wrapper = new ViewWrapper(recyclerView);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(recyclerView, "translationY", getResources().getDimensionPixelSize(R.dimen.dp_300));
        animatorSet.play(animator4);
        animatorSet.setDuration(500).start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height=screenHeight-i;
                recyclerView.setLayoutParams(params);
                vv.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

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

    public void downloadPicture(final String uurl){

        new Thread(new Runnable() {
            @Override
            public void run() {

                URL url = null;
                HttpURLConnection con = null;
                try {

                    url = new URL(uurl);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setReadTimeout(5000);
                    con.setDoInput(true);
                    double length=con.getContentLength();
                    String filename = Environment.getExternalStorageDirectory() +"/savedPic/";
                    File file = new File(filename);
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    File f = new File(file,String.valueOf(System.currentTimeMillis()+".jpg"));
                    final String fname=f.getName();
                    Uri uri = null;
                    if(Build.VERSION.SDK_INT>= 24){
                        uri = FileProvider.getUriForFile(VideoActivity.this, "com.ycs.servicetest.provider", f);
                    }else{
                        uri= Uri.fromFile(f);
                    }
                    OutputStream fos= (OutputStream) getContentResolver().openOutputStream(uri);
//                    FileOutputStream fos = new FileOutputStream(file);
                    InputStream in = con.getInputStream();
                    byte ch[] = new byte[2 * 1024];
                    int len=0;
                    long haswrite=0L;
                    int percent=0;
                    if (fos!= null){
                        while ((len = in.read(ch)) != -1){
                            fos.write(ch,0,len);
                            haswrite += len;
                            //BidDecimal精确数
                            double d = (new BigDecimal(haswrite / length)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
                            percent = (int) (d * 100);
                            Log.e("yyy", "进度"+percent);
                            MainService.updateProgress(VideoActivity.this,percent);

                        }
//                        if(percent==100){
//                            Looper.prepare();
//                            mhandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(VideoActivity.this, fname+"下载成功", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//                        }
                        in.close();
                        fos.close();

                    }else{
                        Log.e("yyy", "fou为空" );
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e("yyy","失败"+e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("yyy","失败"+e.getMessage());
                }
            }
        }).start();
    }
    public void sort(ArrayList<Items> stus){
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
    public void sortSrcList(ArrayList<String> stus){
        Collections.sort(stus, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                // 升序
                //return o1.getAge()-o2.getAge();
                return o2.compareTo(o1);
                // 降序
                // return o2.getAge()-o1.getAge();
                // return o2.getAge().compareTo(o1.getAge());
            }
        });
    }
}