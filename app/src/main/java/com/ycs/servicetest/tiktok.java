package com.ycs.servicetest;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.util.ArrayList;
import java.util.List;

public class tiktok extends AppCompatActivity {

    ViewPager2 viewPager2;

    List<VideoModel> dataList = new ArrayList<>();

    int posision;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tiktok_activity);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dataList=(ArrayList<VideoModel>)getIntent().getSerializableExtra("list");
        posision=getIntent().getIntExtra("i",0);
        Log.e("hhh", dataList.toString() );
        Log.e("hhh",posision+"");
        viewPager2=findViewById(R.id.view_pager2);
        resolveData();
        viewPagerAdapter = new ViewPagerAdapter(this, dataList);

        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager2.setAdapter(viewPagerAdapter);
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

                        playPosition(position);
                    }
                }
            }
        });
        viewPager2.post(new Runnable() {
            @Override
            public void run() {
               // playNewPosition(posision);
                playPosition(0);
            }
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
        GSYVideoManager.releaseAllVideos();
    }

    private void resolveData() {
//        for (int i = 0; i < 19; i++) {
//            Items items = new Items();
//            items.setUrl(Environment.getExternalStorageDirectory() +"/123/VID_20210105_192402.mp4");
//            items.setText("jjhhdd");
//            dataList.add(items);
//        }
//        dataList=itemList;
        if (viewPagerAdapter != null)
            viewPagerAdapter.notifyDataSetChanged();
    }

    private void playPosition(int position) {
        RecyclerView.ViewHolder viewHolder = ((RecyclerView) viewPager2.getChildAt(0)).findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            RecyclerItemNormalHolder recyclerItemNormalHolder = (RecyclerItemNormalHolder) viewHolder;
            recyclerItemNormalHolder.getPlayer().setUp(dataList.get(position).getUrl(),true,"");
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
