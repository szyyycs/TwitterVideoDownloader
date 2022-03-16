package com.ycs.mvvmtest.MVVM;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.ycs.mvctest.MVVM.VideoItem;
import com.ycs.mvvmtest.R;


import java.util.List;

public class MvvmActivity extends AppCompatActivity {
    private VideoViewModel videoViewModel;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvvm);
        mContext=MvvmActivity.this;
        Button re=findViewById(R.id.btn);
        ZXingLibrary.initDisplayOpinion(this);
        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);
      //  videoViewModel = ViewModelProviders.of(this).get(VideoViewModel.class);
        videoViewModel.showList().observe(this, new Observer<List<VideoItem>>() {
            @Override
            public void onChanged(List<VideoItem> videoItems) {
                if(videoItems==null){
                   // Toast.makeText(mContext, "获取失败！", Toast.LENGTH_SHORT).show();
                    return;
                }
             //   Toast.makeText(mContext,videoItems.get(0).getName(), Toast.LENGTH_SHORT).show();
            }
        });
        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //videoViewModel.itemListLD.setValue(null);
                Intent intent = new Intent(MvvmActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 00);
            }
        });
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (null != data) {
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return;
            }
            if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                String result = bundle.getString(CodeUtils.RESULT_STRING);
                Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
            } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                Toast.makeText(MvvmActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(mContext, "11", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void initView(){

    }
}