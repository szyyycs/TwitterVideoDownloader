package com.ycs.mvvmtest.MVVM;

import com.ycs.mvctest.MVVM.VideoItem;

import java.util.List;

public interface ShowListImp {
    void onSuccess(List<VideoItem> list);
    void onFail(String name);
}
