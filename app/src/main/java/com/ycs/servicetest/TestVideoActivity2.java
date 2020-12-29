package com.ycs.servicetest;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.GSYADVideoPlayer;
import com.shuyu.gsyvideoplayer.video.GSYSampleADVideoPlayer;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.ArrayList;
import java.util.List;

public class TestVideoActivity2 extends AppCompatActivity {
    private boolean isPlay;
    private boolean isPause;
    private OrientationUtils orientationUtils;
    MyVideoPlayer detailPlayer;

    String url= Environment.getExternalStorageDirectory() +"/savedPic/20201120114245367c.mp4";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.layout_video2);
        detailPlayer = (MyVideoPlayer) findViewById(R.id.detail_player);
        ImageView imageView=new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Bitmap b = ThumbnailUtils.createVideoThumbnail(url, MediaStore.Images.Thumbnails.MINI_KIND);
        imageView.setImageBitmap(b);
        detailPlayer.getTitleTextView().setVisibility(View.GONE);
        detailPlayer.getBackButton().setVisibility(View.GONE);
        orientationUtils=new OrientationUtils(this,detailPlayer);
        orientationUtils.setEnable(false);
       // detailPlayer.setUp(url, true, "");
        GSYVideoOptionBuilder gsyVideoOption=new GSYVideoOptionBuilder();
        gsyVideoOption.setThumbImageView(imageView)
                .setIsTouchWiget(true)
                .setRotateViewAuto(true)
                .setLockLand(false)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setUrl(url)
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
                    public void onComplete(String url, Object... objects) {
                        super.onComplete(url, objects);

                    }

                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        if(orientationUtils!=null){
                            orientationUtils.backToProtVideo();

                        }
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
               // orientationUtils.resolveByClick();
                detailPlayer.startWindowFullscreen(TestVideoActivity2.this,false,true);
//                detailPlayer.getTitleTextView().setVisibility(View.GONE);
//                detailPlayer.getBackButton().setVisibility(View.GONE);
            }
        });
        detailPlayer.getFullscreenButton().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                orientationUtils.resolveByClick();
                detailPlayer.startWindowFullscreen(TestVideoActivity2.this,false,true);
                return false;
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
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
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            detailPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }


}
