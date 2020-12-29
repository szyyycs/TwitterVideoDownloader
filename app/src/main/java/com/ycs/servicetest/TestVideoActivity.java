package com.ycs.servicetest;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.GSYADVideoPlayer;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.File;

public class TestVideoActivity extends GSYBaseActivityDetail{
    StandardGSYVideoPlayer detailPlayer;
    //OrientationUtils orientationUtils;
      @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_video);
        getSupportActionBar().hide();
        detailPlayer = (StandardGSYVideoPlayer) findViewById(R.id.detail_player);
        initVideoBuilderMode();
    }

    @Override
    public StandardGSYVideoPlayer getGSYVideoPlayer() {
        return detailPlayer;
    }

    @Override
    public GSYVideoOptionBuilder getGSYVideoOptionBuilder() {
        ImageView imageView = new ImageView(this);
        String url=Environment.getExternalStorageDirectory() +"/savedPic/20201120114245367c.mp4";
        Bitmap b = ThumbnailUtils.createVideoThumbnail(Environment.getExternalStorageDirectory() +"/savedPic/20201120114245367c.mp4",
                MediaStore.Images.Thumbnails.MINI_KIND);
        imageView.setImageBitmap(b);

//        orientationUtils = new OrientationUtils(this, detailPlayer);
////初始化不打开外部的旋转
//        orientationUtils.setEnable(false);
        detailPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //orientationUtils.resolveByClick();

                detailPlayer.startWindowFullscreen(TestVideoActivity.this, false, true);
            }
        });

        return new GSYVideoOptionBuilder()
                .setThumbImageView(imageView)
                .setUrl(url)
                .setCacheWithPlay(true)
                .setVideoTitle("噜噜噜")
                .setIsTouchWiget(true)
                .setAutoFullWithSize(false)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setSeekRatio(1);
    }

    @Override
    public void clickForFullScreen() {
        //getSupportActionBar().hide();
    }

    @Override
    public boolean getDetailOrientationRotateAuto() {
        return true;
    }
}
