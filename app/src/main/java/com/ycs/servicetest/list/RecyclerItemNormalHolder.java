package com.ycs.servicetest.list;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.ycs.servicetest.R;
import com.ycs.servicetest.model.VideoModel;
import com.ycs.servicetest.view.CustomCoverVideo;

import java.lang.ref.WeakReference;

public class RecyclerItemNormalHolder extends RecyclerItemBaseHolder {
    public final static String TAG = "GSYVideoBaseManager";
    protected WeakReference<Context> context;
    CustomCoverVideo gsyVideoPlayer;
    GSYVideoOptionBuilder gsyVideoOptionBuilder;
    ImageView imageView = null;

    public RecyclerItemNormalHolder(Context context, View v) {
        super(v);
        this.context = new WeakReference<>(context);
        gsyVideoPlayer = v.findViewById(R.id.video_item_player);
        gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
    }

    public void onBind(int position, VideoModel vm) {
        imageView = new ImageView(context.get());
        Glide.with(context.get())
                .setDefaultRequestOptions(
                        new RequestOptions()
                                //提取视频哪一帧
                                .frame(0)
                                .centerInside()
                )
                .load(vm.getUrl())
                .into(imageView);
        gsyVideoOptionBuilder
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
                        if (!gsyVideoPlayer.isIfCurrentIsFullscreen()) {
                            //非静音
                            GSYVideoManager.instance().setNeedMute(false);
                        }

                    }

                    @Override
                    public void onStartPrepared(String url, Object... objects) {
                        super.onStartPrepared(url, objects);
                    }

                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        super.onAutoComplete(url, objects);
                        gsyVideoPlayer.restart();
                    }
                })
                .build(gsyVideoPlayer);
        gsyVideoPlayer.setUp(vm.getUrl(), false, "");
        if (vm.getTweet() != null) {
            gsyVideoPlayer.setTweetTv(vm.getTweet());
        }

    }

    /**
     * 全屏幕按键处理
     */
    public CustomCoverVideo getPlayer() {
        return gsyVideoPlayer;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
