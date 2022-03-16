package com.ycs.mvctest.MVVM;

import java.util.ArrayList;
import java.util.List;
//model用于数据获取
public class VideoModel implements VideoModelListener {
    @Override
    public void getList(ShowListImp showListImp) {

        VideoItem v=new VideoItem("视频名字","www.baidu.com","2m30s","2.1M");
        List<VideoItem> list=new ArrayList<>();
        for(int i=0;i<4;i++){
            list.add(v);
        }
        showListImp.onSuccess(list);
    }


}
