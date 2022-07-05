package com.ycs.servicetest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager2.widget.ViewPager2;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.ycs.servicetest.list.RecyclerItemNormalHolder;
import com.ycs.servicetest.list.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class Tiktok extends AppCompatActivity {

    ViewPager2 viewPager2;

    List<VideoModel> dataList = new ArrayList<>();
    List<VideoModel> showList = new ArrayList<>();
    int onePageLen = 4;
    int subIndex = onePageLen;
    View foot;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tiktok_activity);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dataList = (ArrayList<VideoModel>) getIntent().getSerializableExtra("list");
        //showList=dataList.subList(0,20);
        viewPager2 = findViewById(R.id.view_pager2);
        //resolveData();
        viewPagerAdapter = new ViewPagerAdapter(this, dataList);
        viewPager2.setOffscreenPageLimit(1);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager2.setAdapter(viewPagerAdapter);
        //foot = LayoutInflater.from(this).inflate(R.layout.foot_view,null);
        setSupportsChangeAnimations(viewPager2, false);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //大于0说明有播放
                int playPosition = GSYVideoManager.instance().getPlayPosition();
                if (playPosition >= 0) {
                    //对应的播放列表TAG
                    if (GSYVideoManager.instance().getPlayTag().equals(RecyclerItemNormalHolder.TAG)
                            && (position != playPosition)) {
                        Log.d("kadun", "onPageSelected: " + position + ";  playPosition:" + playPosition);
                        viewPager2.postDelayed(() -> playPosition(position), 300);
                    }
                }
            }
        });
        viewPager2.post(() -> {
            playPosition(0);
            Log.d("kadun", "playPosition(0)");
        });
    }

    @Override
    public void onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GSYVideoManager.onResume(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("yyy", "releaseAll");
        GSYVideoManager.releaseAllVideos();
    }

    public void setSupportsChangeAnimations(ViewPager2 viewPager, boolean enable) {

        for (int i = 0; i < viewPager.getChildCount(); i++) {

            View view = viewPager.getChildAt(i);

            if (view instanceof RecyclerView) {

                RecyclerView.ItemAnimator animator = ((RecyclerView) view).getItemAnimator();

                if (animator != null) {

                    ((SimpleItemAnimator) animator).setSupportsChangeAnimations(enable);

                }

                break;

            }

        }

    }

    private void resolveData() {

        if (viewPagerAdapter != null)
            viewPagerAdapter.notifyDataSetChanged();
    }

    private void playPosition(int position) {
        RecyclerView.ViewHolder viewHolder = ((RecyclerView) viewPager2.getChildAt(0)).findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            RecyclerItemNormalHolder recyclerItemNormalHolder = (RecyclerItemNormalHolder) viewHolder;
            recyclerItemNormalHolder.getPlayer().startPlayLogic();
        }
    }

    private void playNewPosition(int position) {
//        if(viewPager2.getChildAt(0)==null){
//            Log.e("yyy","getChildAt(position)空");
//        }else if(((RecyclerView) viewPager2.getChildAt(0)).findViewHolderForAdapterPosition(position)==null){
//            Log.e("yyy","((RecyclerView) viewPager2.getChildAt(position)).findViewHolderForAdapterPosition(position)==null空");
//        }

        RecyclerView.ViewHolder viewHolder = ((RecyclerView) viewPager2.getChildAt(0)).findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            RecyclerItemNormalHolder recyclerItemNormalHolder = (RecyclerItemNormalHolder) viewHolder;
            recyclerItemNormalHolder.getPlayer().setUp(dataList.get(position).getUrl(),true,"");
            recyclerItemNormalHolder.getPlayer().startPlayLogic();
        }
    }

}
