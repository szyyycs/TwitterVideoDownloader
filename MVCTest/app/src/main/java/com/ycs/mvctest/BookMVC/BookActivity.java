package com.ycs.mvctest.BookMVC;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.ycs.mvctest.R;

public class BookActivity extends AppCompatActivity {
    private BookController bc;
    private ListView lv_book;
    //bc不持有view
    //view持有bc
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        bc=new BookController();
        Button b=new Button(this);
        b.setOnClickListener(v-> bc.add(() -> {
           // notify();
            //这里编写添加的结果
        }));

    }
}