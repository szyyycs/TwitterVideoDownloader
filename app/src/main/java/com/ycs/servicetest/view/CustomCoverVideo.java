package com.ycs.servicetest.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.ycs.servicetest.R;

import moe.codeest.enviews.ENDownloadView;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

public class CustomCoverVideo extends StandardGSYVideoPlayer {
    ImageView mCoverImage;
    TextView tweetTv;
    TextView speedTv;
    public CustomCoverVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public CustomCoverVideo(Context context) {
        super(context);
    }

    public CustomCoverVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mCoverImage = findViewById(R.id.thumbImage);
        tweetTv = findViewById(R.id.text);
        // PlayerFactory.setPlayManager(SystemPlayerManager.class);
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);//EXO模式
        //PlayerFactory.setPlayManager(IjkPlayerManager.class);//ijk模式
        speedTv = findViewById(R.id.speed_tv);
        if (mThumbImageViewLayout != null &&
                (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)) {
            mThumbImageViewLayout.setVisibility(VISIBLE);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.video_layout_cover;
    }


    @Override
    public GSYBaseVideoPlayer showSmallVideo(Point size, boolean actionBar, boolean statusBar) {
        //下面这里替换成你自己的强制转化
        CustomCoverVideo customCoverVideo = (CustomCoverVideo) super.showSmallVideo(size, actionBar, statusBar);
        customCoverVideo.mStartButton.setVisibility(GONE);
        customCoverVideo.mStartButton = null;
        return customCoverVideo;
    }

    @Override
    protected void cloneParams(GSYBaseVideoPlayer from, GSYBaseVideoPlayer to) {
        super.cloneParams(from, to);
        CustomCoverVideo sf = (CustomCoverVideo) from;
        CustomCoverVideo st = (CustomCoverVideo) to;
        st.mShowFullAnimation = sf.mShowFullAnimation;
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        Toast.makeText(mContext, "放开", Toast.LENGTH_SHORT).show();
        startPlay();
    }

    /**
     * 退出window层播放全屏效果
     */
    @SuppressWarnings("ResourceType")
    @Override
    protected void clearFullscreenLayout() {
        if (!mFullAnimEnd) {
            return;
        }
        mIfCurrentIsFullscreen = false;
        int delay = 0;
        if (mOrientationUtils != null) {
            delay = mOrientationUtils.backToProtVideo();
            mOrientationUtils.setEnable(false);
            if (mOrientationUtils != null) {
                mOrientationUtils.releaseListener();
                mOrientationUtils = null;
            }
        }

        if (!mShowFullAnimation) {
            delay = 0;
        }

        final ViewGroup vp = (CommonUtil.scanForActivity(getContext())).findViewById(Window.ID_ANDROID_CONTENT);
        final View oldF = vp.findViewById(getFullId());
        if (oldF != null) {
            //此处fix bug#265，推出全屏的时候，虚拟按键问题
            CustomCoverVideo gsyVideoPlayer = (CustomCoverVideo) oldF;
            gsyVideoPlayer.mIfCurrentIsFullscreen = false;
        }

        if (delay == 0) {
            backToNormal();
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    backToNormal();
                }
            }, delay);
        }

    }

    @Override
    protected void touchSurfaceUp() {
        super.touchSurfaceUp();
        getCurrentPlayer().setSpeedPlaying(1f, true);
        startDismissControlViewTimer();
        dismissProgressDialog();
        speedTv.setVisibility(GONE);

    }

    @Override
    protected void touchLongPress(MotionEvent e) {
        super.touchLongPress(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mCurrentState == CURRENT_STATE_PAUSE) {
                    break;
                }
                speedTv.setVisibility(VISIBLE);
                getCurrentPlayer().setSpeedPlaying(2f, false);
                // Toast.makeText(mContext, "×2", Toast.LENGTH_SHORT).show();
                cancelDismissControlViewTimer();

                break;
            case MotionEvent.ACTION_UP:
                if (mCurrentState == CURRENT_STATE_PAUSE) {
                    break;
                }
                getCurrentPlayer().setSpeedPlaying(1f, false);
                break;
            default:

        }

    }

    public void restart() {
        mStartButton.setVisibility(INVISIBLE);
        setViewShowState(mLockScreen, GONE);
        hideAllWidget();
        //setSeekOnStart(2000);
        startPlay();

    }

    public void startPlay() {
        getStartButton().performClick();
        postDelayed(() -> hideAllWidget(), 400);
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
        setViewShowState(tweetTv, VISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);
        if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }
        updateStartImage();
    }

    @Override
    protected void onClickUiToggle() {
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
                getStartButton().performClick();
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    //changeUiToPlayingClear();
                    changeUiToPauseShow();
                } else {
                    //changeUiToPlayingPause();
                    changeUiToPauseShow();
                }
            }
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            if (mBottomContainer != null) {
                getStartButton().performClick();
                if (mBottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPauseClear();
                } else {
                    changeUiToPlayingClear();
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

    @Override
    protected void changeUiToPauseShow() {
        Debuger.printfLog("changeUiToPauseShow");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, VISIBLE);
        setViewShowState(mStartButton, VISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mThumbImageViewLayout, INVISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(tweetTv, INVISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);

        if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }
        updateStartImage();
        // updatePauseCover();
    }

    protected void updateStartImage() {
        if (mStartButton instanceof ImageView) {
            ImageView imageView = (ImageView) mStartButton;
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                imageView.setImageResource(R.drawable.video_click_pause_selector);
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                imageView.setImageResource(R.drawable.video_click_error_selector);
            } else {
                imageView.setImageResource(R.drawable.play);
                imageView.setImageAlpha(70);
            }
        }
    }

    protected void changeUiToPlayingPause() {
        Debuger.printfLog("changeUiToPlayingShow");
        setViewShowState(tweetTv, INVISIBLE);
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
        // startPlay();
    }
    @Override
    protected void changeUiToNormal() {
        Debuger.printfLog("changeUiToNormal");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(tweetTv, VISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mThumbImageViewLayout, VISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);

        updateStartImage();
        if (mLoadingProgressBar instanceof ENDownloadView) {
            ((ENDownloadView) mLoadingProgressBar).reset();
        }
        byStartedClick = false;
    }

    /******************* 下方两个重载方法，在播放开始前不屏蔽封面，不需要可屏蔽 ********************/
    @Override
    public void onSurfaceUpdated(Surface surface) {
        super.onSurfaceUpdated(surface);
        if (mThumbImageViewLayout != null && mThumbImageViewLayout.getVisibility() == VISIBLE) {
            mThumbImageViewLayout.setVisibility(INVISIBLE);
        }
    }

    @Override
    protected void setViewShowState(View view, int visibility) {
        if (view == mThumbImageViewLayout && visibility != VISIBLE) {
            return;
        }
        super.setViewShowState(view, visibility);
    }

    @Override
    public void onSurfaceAvailable(Surface surface) {
        super.onSurfaceAvailable(surface);
        if (GSYVideoType.getRenderType() != GSYVideoType.TEXTURE) {
            if (mThumbImageViewLayout != null && mThumbImageViewLayout.getVisibility() == VISIBLE) {
                mThumbImageViewLayout.setVisibility(INVISIBLE);
            }
        }
    }

    /******************* 下方重载方法，在播放开始不显示底部进度和按键，不需要可屏蔽 ********************/

    protected boolean byStartedClick;

//    @Override
//    protected void onClickUiToggle() {
//        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
//            setViewShowState(mLockScreen, VISIBLE);
//            return;
//        }
//        byStartedClick = true;
//        super.onClickUiToggle();
//
//    }


    @Override
    protected void changeUiToPreparingShow() {
        super.changeUiToPreparingShow();
        Debuger.printfLog("Sample changeUiToPreparingShow");
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(tweetTv, VISIBLE);
    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow();
        Debuger.printfLog("Sample changeUiToPlayingBufferingShow");
        if (!byStartedClick) {
            setViewShowState(mBottomContainer, INVISIBLE);
            setViewShowState(mStartButton, INVISIBLE);
            setViewShowState(tweetTv, VISIBLE);
        }
    }

//    @Override
//    protected void changeUiToPlayingShow() {
//        super.changeUiToPlayingShow();
//        Debuger.printfLog("Sample changeUiToPlayingShow");
//        if (!byStartedClick) {
//            setViewShowState(mBottomContainer, INVISIBLE);
//            setViewShowState(mStartButton, INVISIBLE);
//        }
//    }

    @Override
    public void startAfterPrepared() {
        super.startAfterPrepared();
        Debuger.printfLog("Sample startAfterPrepared");
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(tweetTv, VISIBLE);
        setViewShowState(mBottomProgressBar, VISIBLE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mDownX = x;
                mDownY = y;
                // 禁止parent拦截down事件
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float deltaX = x - mDownX;
                float deltaY = y - mDownY;
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);
                if (absDeltaY > 2 * absDeltaX) { // 根据需求条件来决定是否让Parent View拦截事件。
                    getParent().requestDisallowInterceptTouchEvent(false);//上滑
                    Log.d("yyyy", "dispatchTouchEvent: false");
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);//左右
                    Log.d("yyyy", "dispatchTouchEvent: true");
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                dismissProgressDialog();
                break;
            }
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void touchSurfaceMove(float deltaX, float deltaY, float y) {
        int curWidth = 0;
        int curHeight = 0;
        if (getActivityContext() != null) {
            curWidth = CommonUtil.getCurrentScreenLand((Activity) getActivityContext()) ? mScreenHeight : mScreenWidth;
            curHeight = CommonUtil.getCurrentScreenLand((Activity) getActivityContext()) ? mScreenWidth : mScreenHeight;
        }
        if (mChangePosition) {
            int totalTimeDuration = getDuration();
            mSeekTimePosition = (int) (mDownPosition + (deltaX * totalTimeDuration / curWidth) / mSeekRatio);
            if (mSeekTimePosition > totalTimeDuration)
                mSeekTimePosition = totalTimeDuration;
            String seekTime = CommonUtil.stringForTime(mSeekTimePosition);
            String totalTime = CommonUtil.stringForTime(totalTimeDuration);
            showProgressDialog(deltaX, seekTime, mSeekTimePosition, totalTime, totalTimeDuration);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        byStartedClick = true;
        super.onStartTrackingTouch(seekBar);
    }

    public String getTweetText() {
        return tweetTv.getText().toString();
    }

    public void setTweetTv(String text) {
        tweetTv.setText(text);
    }
}
