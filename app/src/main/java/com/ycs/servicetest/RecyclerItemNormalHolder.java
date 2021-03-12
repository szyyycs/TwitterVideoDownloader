package com.ycs.servicetest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.disklrucache.DiskLruCache;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RecyclerItemNormalHolder extends RecyclerItemBaseHolder{
    public final static String TAG = "yyy";

    protected Context context;
    private String url=Environment.getExternalStorageDirectory() +"/.savedPic/";
    private DiskLruCache mDiskCache;
    SampleCoverVideo gsyVideoPlayer;
    ImageView imageView;

    GSYVideoOptionBuilder gsyVideoOptionBuilder;

    public RecyclerItemNormalHolder(Context context, View v) {
        super(v);
        this.context = context;
        gsyVideoPlayer=v.findViewById(R.id.video_item_player);
        SharedPreferences spp=context.getSharedPreferences("url",Context.MODE_PRIVATE);
        if(!spp.getString("url","").equals("")){
            url=spp.getString("url","");
        }
        imageView = new ImageView(context);
        File ff=new File(url);
        try {
            mDiskCache= DiskLruCache.open(ff, 1, 1,   1024 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
    }
//    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
//        if (getBitmapFromMemCache(key) == null) {
//            mMemoryCache.put(key, bitmap);
//        }
//    }
//    public synchronized void loadBitmap(String imageKey, ImageView imageView) {
//        Bitmap bitmap = getBitmapFromMemCache(imageKey);
//        if (bitmap != null) {
//            imageView.setImageBitmap(bitmap);
//        } else {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(imageKey, MediaStore.Images.Thumbnails.MINI_KIND);
//                    //imageView.setImageBitmap(bitmap);
//                    //addBitmapToMemoryCache(imageKey,bitmap);
//                }
//            }).start();
//        }
//    }

        public void onBind(final int position, VideoModel vm) {
            Log.e(TAG, "vm.getUrl(): "+vm.getUrl() );
            imageView.setImageBitmap(getCache(vm.getUrl()));
            //imageView.setImageResource(R.mipmap.blank);
            gsyVideoOptionBuilder
                    .setIsTouchWiget(false)
                    .setThumbImageView(imageView)
    //                .setUrl(url)
                    //.setVideoTitle(title)
                    .setCacheWithPlay(true)
                    .setRotateViewAuto(true)
                    .setLockLand(true)
                    .setPlayTag(TAG)
                    .setLooping(true)
                    .setShowFullAnimation(true)
                    .setNeedLockFull(true)
                    .setPlayPosition(position)
                    .setVideoAllCallBack(new GSYSampleCallBack() {
                        @Override
                        public void onPrepared(String url, Object... objects) {
                            super.onPrepared(url, objects);
                            if (!gsyVideoPlayer.isIfCurrentIsFullscreen()) {
                                //非静音
                                GSYVideoManager.instance().setNeedMute(false);
                            }

                        }

                        @Override
                        public void onAutoComplete(String url, Object... objects) {
                            super.onAutoComplete(url, objects);
                            gsyVideoPlayer.restart();
                        }

                        @Override
                        public void onQuitFullscreen(String url, Object... objects) {
                            super.onQuitFullscreen(url, objects);
                            //全屏不静音
                            GSYVideoManager.instance().setNeedMute(false);

                        }

                        @Override
                        public void onEnterFullscreen(String url, Object... objects) {
                            super.onEnterFullscreen(url, objects);
                            GSYVideoManager.instance().setNeedMute(false);
                            gsyVideoPlayer.getCurrentPlayer().getTitleTextView().setText((String) objects[0]);
                        }
                    }).build(gsyVideoPlayer);


            //增加title
            gsyVideoPlayer.getTitleTextView().setVisibility(View.GONE);

            //设置返回键
            gsyVideoPlayer.getBackButton().setVisibility(View.GONE);
            //设置全屏按键功能
            gsyVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //resolveFullBtn(gsyVideoPlayer);
                }
            });
        //gsyVideoPlayer.loadCoverImageBy(R.mipmap.blank, R.mipmap.default_pic);
    }

    /**
     * 全屏幕按键处理
     */
    private void resolveFullBtn(final StandardGSYVideoPlayer standardGSYVideoPlayer) {
        standardGSYVideoPlayer.startWindowFullscreen(context, false, false);
    }
//    public Bitmap getBitmapFromMemCache(String key) {
//        return mMemoryCache.get(key);
//    }
    public  SampleCoverVideo getPlayer() {
        return gsyVideoPlayer;
    }
    private Bitmap getCache(String key) {
//        Log.e(TAG, "get" );
        key=hashKeyForDisk(key);

        try {
            DiskLruCache.Snapshot snapshot = mDiskCache.get(key);

            if (snapshot != null) {
                InputStream in = snapshot.getInputStream(0);

                return BitmapFactory.decodeStream(in);
            }

            //Log.e(TAG, "nullnulllllll " );
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();

        }

        return null;
    }
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");//把uri编译为MD5,防止网址有非法字符
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        //Log.e(TAG, "cacheKey: "+cacheKey);
        return cacheKey;
    }
    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
