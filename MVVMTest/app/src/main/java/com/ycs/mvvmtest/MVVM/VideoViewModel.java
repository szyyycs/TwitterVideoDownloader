package com.ycs.mvvmtest.MVVM;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ycs.mvctest.MVVM.VideoItem;

import java.util.ArrayList;
import java.util.List;

public class VideoViewModel extends AndroidViewModel {
    Context mContext;
    MutableLiveData<List<VideoItem>> itemListLD= new MutableLiveData<>();
    MutableLiveData<String> nameLD=new MutableLiveData<String>();
    public VideoViewModel(@NonNull Application application) {
        super(application);
        mContext=application;
    }
    public LiveData<List<VideoItem>> showList(){
        List<VideoItem> itemList=new ArrayList<>();
        itemListLD.postValue(itemList);
        VideoModel videoModel=new VideoModel();
        videoModel.getList(new ShowListImp() {
            @Override
            public void onSuccess(List<VideoItem> list) {
                itemListLD.postValue(list);
            }

            @Override
            public void onFail(String name) {
                itemListLD.postValue(null);
                nameLD.postValue(name);
            }
        });
        return itemListLD;
    }

}
