package com.ycs.mvctest.MVVM;

import android.app.Application;
import android.content.Context;
import android.database.Observable;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class VideoVM extends AndroidViewModel {
    Context mContext;
    MutableLiveData<List<VideoItem>> itemListLD= new MutableLiveData<>();
    public VideoVM(@NonNull Application application) {
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
            public void onFail() {

            }
        });
        return itemListLD;
    }

}
