package com.ycs.servicetest.list;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.ycs.servicetest.R;
import com.ycs.servicetest.SampleCoverVideo;
import com.ycs.servicetest.VideoModel;

import java.lang.ref.WeakReference;

public class RecyclerItemNormalHolder extends RecyclerItemBaseHolder {
    public final static String TAG = "GSYVideoBaseManager";
    protected WeakReference<Context> context;
    SampleCoverVideo gsyVideoPlayer;
    GSYVideoOptionBuilder gsyVideoOptionBuilder;
    ImageView imageView = null;

    public RecyclerItemNormalHolder(Context context, View v) {
        super(v);
        this.context = new WeakReference<>(context);
        gsyVideoPlayer = v.findViewById(R.id.video_item_player);
        gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
    }
        public void onBind(final int position, VideoModel vm) {
            Log.e(TAG, "vm.getUrl(): " + vm.getUrl() + "position" + position);
            if (imageView == null) {
                imageView = new ImageView(context.get());
                Log.d("ViewPager2", "holder里第" + position + "项初始化 ");
                Glide.with(context.get())
                        .setDefaultRequestOptions(
                                new RequestOptions()
                                        //提取视频哪一帧
                                        .frame(0)
                                        .centerInside()
                        )
                        .load(vm.getUrl())
                        .into(imageView);
            }
            gsyVideoOptionBuilder
                    .setIsTouchWiget(false)
                    .setThumbImageView(imageView)
                    .setCacheWithPlay(true)
                    .setPlayTag(TAG)
                    .setLooping(true)
                    .setNeedShowWifiTip(false)
                    .setPlayPosition(position)
                    .setVideoAllCallBack(new GSYSampleCallBack() {
                        @Override
                        public void onPrepared(String url, Object... objects) {
                            super.onPrepared(url, objects);
                            imageView = null;
                            Log.e(TAG, "image=null");
                            if (!gsyVideoPlayer.isIfCurrentIsFullscreen()) {
                                //非静音
                                GSYVideoManager.instance().setNeedMute(false);
                            }

                        }

                        @Override
                        public void onStartPrepared(String url, Object... objects) {
                            super.onStartPrepared(url, objects);
                            Log.e(TAG, "onStartPrepared");
                        }

                        @Override
                        public void onAutoComplete(String url, Object... objects) {
                            super.onAutoComplete(url, objects);
                            gsyVideoPlayer.restart();
                        }
                    })
                    .build(gsyVideoPlayer);
            gsyVideoPlayer.setUp(vm.getUrl(), false, "");
        }

    /**
     * 全屏幕按键处理
     */
    public SampleCoverVideo getPlayer() {
        return gsyVideoPlayer;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
