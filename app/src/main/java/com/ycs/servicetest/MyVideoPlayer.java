package com.ycs.servicetest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
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
    private Vibrator vi;
    private boolean mSpeed=false;
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

    @Override
    protected void touchSurfaceUp() {
        super.touchSurfaceUp();
        if(mSpeed){
            mSpeed=false;
            getCurrentPlayer().setSpeedPlaying(speed, true);
            changeSpeed.setText(""+speed);
            startDismissControlViewTimer();
           // Log.d("yyy", "touchLongPress: up");
        }
    }

    @Override
    protected void touchLongPress(MotionEvent e) {
        super.touchLongPress(e);
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(vi!=null){
                    vi.vibrate(50);
                }
                mSpeed=true;
                getCurrentPlayer().setSpeedPlaying(2f, false);
                changeSpeed.setText("加速");
                cancelDismissControlViewTimer();
              //  Log.d("yyy", "touchLongPress: down");
                break;
            case MotionEvent.ACTION_UP:

                break;
            default:

        }

    }
    public void getVibrate(Vibrator vibrator){
        this.vi=vibrator;
    }
    @SuppressLint("ClickableViewAccessibility")
    void initView(){
        nextVideo=findViewById(R.id.next);
        changeSpeed = (TextView) findViewById(R.id.switchSize);

//        thumb.setOnTouchListener((v, event) -> {
//            Log.d("yyy", "onTouch: 美紫紫");
//            switch (event.getAction()){
//                case MotionEvent.ACTION_DOWN:
//                    changeSpeed.setText("2");
//                    Log.d("yyy", "onTouch: 2");
//                    getCurrentPlayer().setSpeedPlaying(2, true);
//                    break;
//                case MotionEvent.ACTION_UP:
//                    changeSpeed.setText(""+speed);
//                    getCurrentPlayer().setSpeedPlaying(speed, true);
//                    break;
//                default:
//
//            }
//            return false;
//        });
        changeSpeed.setOnClickListener(v -> resolveTypeUI());

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
        } else if (speed == 2f) {
            speed = 0.5f;
        } else if (speed == 0.5f) {
            speed = 1;
        }
        changeSpeed.setText(""+speed);
        getCurrentPlayer().setSpeedPlaying(speed, true);
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

    public void startPlay(){
        getStartButton().performClick();
        postDelayed(() -> hideAllWidget(), 400);
    }

}
