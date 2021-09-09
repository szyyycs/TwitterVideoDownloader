package com.ycs.servicetest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import moe.codeest.enviews.ENDownloadView;

public class MyVideoPlayer extends StandardGSYVideoPlayer {
    private float speed=1;
    TextView changeSpeed;
    ImageView nextVideo;
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
        nextVideo=findViewById(R.id.next);
        changeSpeed = (TextView) findViewById(R.id.switchSize);
        changeSpeed.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               resolveTypeUI();
           }
       });

    }
    public ImageView getNextVideo(){
        return nextVideo;
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

    @Override
    protected void changeUiToPlayingShow() {
        Debuger.printfLog("changeUiToPlayingShow");
        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mThumbImageViewLayout, INVISIBLE);
        setViewShowState(mBottomProgressBar, VISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);
        if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }
        updateStartImage();
    }
    @Override
    protected void onClickUiToggle() {
        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
            setViewShowState(mLockScreen, VISIBLE);
            return;
        }
        if (mCurrentState == CURRENT_STATE_PREPAREING) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPrepareingClear();
                } else {
                    changeUiToPreparingShow();
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PLAYING) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPlayingClear();
                } else {
                    changeUiToPlayingPause();
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPauseClear();
                } else {
                    changeUiToPauseShow();
                }
            }
        } else if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToCompleteClear();
                } else {
                    changeUiToCompleteShow();
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PLAYING_BUFFERING_START) {
            if (mBottomContainer != null) {
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPlayingBufferingClear();
                } else {
                    changeUiToPlayingBufferingShow();
                }
            }
        }
    }

    protected void changeUiToPlayingPause() {
        Debuger.printfLog("changeUiToPlayingShow");
        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(mStartButton, VISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mThumbImageViewLayout, INVISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);

        if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }
        updateStartImage();
    }
    public void restart(){
        mStartButton.setVisibility(INVISIBLE);
        setViewShowState(mLockScreen, GONE);
        hideAllWidget();
        //setSeekOnStart(2000);
        startPlay();

    }

    @Override
    public void onAutoCompletion() {

        super.onAutoCompletion();
        changeUiToPlayingClear();
        //mStartButton.setVisibility(INVISIBLE);
    }

//    @Override
//    protected void updateStartImage() {
//        if (mStartButton instanceof ENPlayView) {
//            ENPlayView enPlayView = (ENPlayView) mStartButton;
//            enPlayView.setDuration(500);
//            if (mCurrentState == CURRENT_STATE_PLAYING) {
//                enPlayView.play();
//            } else if (mCurrentState == CURRENT_STATE_ERROR) {
//                enPlayView.pause();
//            } else {
//                enPlayView.pause();
//            }
//        } else if (mStartButton instanceof ImageView) {
//            ImageView imageView = (ImageView) mStartButton;
//            if (mCurrentState == CURRENT_STATE_PLAYING) {
//                imageView.setImageResource(R.drawable.video_click_pause_selector);
//            } else if (mCurrentState == CURRENT_STATE_ERROR) {
//                imageView.setImageResource(R.drawable.video_click_error_selector);
//            } else {
//                imageView.setVisibility(INVISIBLE);
//                imageView.setImageResource(R.drawable.video_click_play_selector);
//            }
//        }
//    }

    public void startPlay(){
        getStartButton().performClick();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                hideAllWidget();
            }
        }, 400);
//        if (mVideoAllCallBack != null) {
//            Debuger.printfLog("onClickStartThumb");
//            mVideoAllCallBack.onClickStartThumb(mOriginUrl, mTitle, MyVideoPlayer.this);
//            //自定义的在开始时发生的事情
//        }
//        //清空progress
//
//        prepareVideo();
//        hideAllWidget();
//        setViewShowState(mLockScreen, GONE);

//        if (mHideKey && mIfCurrentIsFullscreen && mShowVKey) {
//            hideNavKey(mContext);
//        }
//        changeUiToPlayingClear();
//        if (mVideoAllCallBack != null) {
//            mVideoAllCallBack.onClickStartThumb(mOriginUrl, mTitle, MyVideoPlayer.this);
//        }
//        prepareVideo();
//        //setStateAndUi(CURRENT_STATE_PLAYING);
//        cancelDismissControlViewTimer();
//        mPostDismiss = true;
////        setViewShowState(mLockScreen, GONE);
////        hideAllWidget();
//        postDelayed(ttask,500);
    }

}
