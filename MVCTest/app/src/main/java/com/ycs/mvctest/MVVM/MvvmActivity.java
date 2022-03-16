package com.ycs.mvctest.MVVM;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.Toast;

import com.ycs.mvctest.R;

import java.util.ArrayList;
import java.util.List;

public class MvvmActivity extends AppCompatActivity {
    private VideoVM videoVM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvvm);
        videoVM=ViewModelProviders.of(this).get(VideoVM.class);
        videoVM.showList().observe(this,(List<VideoItem> list)->{
            Toast.makeText(this,list.get(0).getName(), Toast.LENGTH_SHORT).show();
        });
    }
    public void initView(){

    }
}