package com.ycs.mvctest.MVVM;

import java.util.List;

public interface ShowListImp {
    void onSuccess(List<VideoItem> list);
    void onFail();
}
