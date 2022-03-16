package com.ycs.mvctest.DataBindingTest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ycs.mvctest.BookMVC.Book;
import com.ycs.mvctest.R;
import com.ycs.mvctest.databinding.ActivityDataBindingBinding;

public class DataBindingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDataBindingBinding binding= DataBindingUtil.setContentView(this,R.layout.activity_data_binding);
        Book book=new Book("三体",R.mipmap.ic_launcher);
        binding.setBook(book);

    }
}