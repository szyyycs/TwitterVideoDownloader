package com.ycs.servicetest;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoControlView;

import static com.shuyu.gsyvideoplayer.utils.CommonUtil.hideNavKey;

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
        changeSpeed.setText(""+speed);
        this.setSpeedPlaying(speed, true);
    }
    @Override
    public int getLayoutId() {
        return R.layout.sample_video_pick;
    }

    @Override
    protected void showWifiDialog() {
        //super.showWifiDialog();
        startPlayLogic();
    }


    public void startPlay(){
//        startPlayLogic();
        if (mVideoAllCallBack != null) {
            mVideoAllCallBack.onClickStartThumb(mOriginUrl, mTitle, MyVideoPlayer.this);
        }
        prepareVideo();
        cancelDismissControlViewTimer();
        mPostDismiss = true;
        postDelayed(ttask,500);
    }
    Runnable ttask = new Runnable() {
        @Override
        public void run() {
            if (mCurrentState != CURRENT_STATE_NORMAL
                    && mCurrentState != CURRENT_STATE_ERROR
                    && mCurrentState != CURRENT_STATE_AUTO_COMPLETE) {
                if (getActivityContext() != null) {
                    Log.e("yyy", "运行一次咯" );
                    hideAllWidget();
                    setViewShowState(mLockScreen, GONE);
                    if (mHideKey && mIfCurrentIsFullscreen && mShowVKey) {
                        hideNavKey(mContext);
                    }
                }
                if (mPostDismiss) {
                    postDelayed(this, 3500);
                }
            }
        }
    };

}
