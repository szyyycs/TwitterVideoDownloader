package com.ycs.servicetest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.video.GSYSampleADVideoPlayer;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

public class MyVideoPlayer extends StandardGSYVideoPlayer {
    private float speed=1;
    TextView changeSpeed;
    public MyVideoPlayer(Context context) {
        super(context);
    }

    public MyVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        initView();
    }
    void initView(){
        changeSpeed = (TextView) findViewById(R.id.switchSize);
       changeSpeed.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               resolveTypeUI();
           }
       });
    }
    private void resolveTypeUI() {
        if (speed == 1) {
            speed = 1.25f;
        }else if(speed == 1.25f){
            speed = 1.5f;
        } else if (speed == 1.5f) {
            speed = 2f;
        } else if (speed == 2) {
            speed = 0.5f;
        } else if (speed == 0.5f) {
            speed = 1;
        }
        changeSpeed.setText("播放速度：" + speed);
        this.setSpeedPlaying(speed, true);
    }
    @Override
    public int getLayoutId() {
        return R.layout.sample_video_pick;
    }

}
