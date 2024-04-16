package com.ycs.servicetest.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.ycs.servicetest.R;

import moe.codeest.enviews.ENDownloadView;



public class CustomVideoPlayer extends StandardGSYVideoPlayer {
    private float speed=1;
    TextView changeSpeed;
    ImageView nextVideo;
    private Vibrator vi;
    private boolean mSpeed=false;
    public CustomVideoPlayer(Context context) {
        super(context);
    }
    public CustomVideoPlayer(Context context, AttributeSet attrs) {
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
            changeSpeed.setText(String.valueOf(speed));
            startDismissControlViewTimer();
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
                break;
            case MotionEvent.ACTION_UP:

                break;
            default:

        }

    }

    public void getVibrate(Vibrator vibrator) {
        this.vi = vibrator;
    }

    @SuppressLint("ClickableViewAccessibility")
    void initView() {
        nextVideo = findViewById(R.id.next);
        changeSpeed = findViewById(R.id.switchSize);
        changeSpeed.setOnClickListener(v -> resolveTypeUI());
        PlayerFactory.setPlayManager(SystemPlayerManager.class);
        //        //EXOPlayer内核，支持格式更多
        //        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        ////系统内核模式
        //        PlayerFactory.setPlayManager(SystemPlayerManager.class);
        ////ijk内核，默认模式
        //        PlayerFactory.setPlayManager(IjkPlayerManager.class);
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
        changeSpeed.setText(String.valueOf(speed));
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
        postDelayed(this::hideAllWidget, 400);
    }

}
