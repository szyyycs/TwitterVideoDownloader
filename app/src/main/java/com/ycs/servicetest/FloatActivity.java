package com.ycs.servicetest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FloatActivity extends AppCompatActivity {
    private Button btn;
    private TextView tv;
    private EditText ed;
    private ImageView line;
    private TextView tvv;
    private Boolean isTextview=true;
    public static boolean isFloatWindowsshow=false;
    private SharedPreferences sp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float);
        getSupportActionBar().hide();
        btn=findViewById(R.id.save);
        tv=findViewById(R.id.tv);
        ed=findViewById(R.id.ed);
        tvv=findViewById(R.id.tvv);
        sp=getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        if(sp.getString("text",null)==null){
            e.putString("text","");
            e.commit();
        }else{
            tvv.setText(sp.getString("text",null));
            tv.setText(sp.getString("text",null));
        }
        ed.setText(sp.getString("text",null));
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvv.setMovementMethod(ScrollingMovementMethod.getInstance());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTextview){
                    tvv.setVisibility(View.GONE);
                    ed.setVisibility(View.VISIBLE);
                    ed.setText(sp.getString("text",null));
                    btn.setText("保存");
                    isTextview=false;
                }else{
                    tvv.setVisibility(View.VISIBLE);
                    ed.setVisibility(View.GONE);

                    btn.setText("编辑");
                    isTextview=true;
                    SharedPreferences.Editor e = sp.edit();
                    e.putString("text",ed.getText().toString());
                    e.commit();
                    tvv.setText(sp.getString("text",null));

                    Toast.makeText(FloatActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new IosAlertDialog(FloatActivity.this).builder()
                        .setTitle("提示")
                        .setMsg("确认删除？")
                        .setPositiveButton("确认", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPreferences.Editor e = sp.edit();
                                String s=sp.getString("text",null);
                                s=s.substring(0,s.length()-1);
                                int i=s.lastIndexOf("\n");
                                if(i<0){
                                    s="";
                                }else{
                                    s=s.substring(0,i);
                                }
                                e.putString("text",s);
                                e.commit();
                                tvv.setText(sp.getString("text",null));
                                ed.setText(sp.getString("text",null));

                                Toast.makeText(FloatActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();
                return false;
            }
        });
        if(!isFloatWindowsshow){
            Intent i=new Intent(FloatActivity.this,FloatWindowService.class);
            startService(i);
            isFloatWindowsshow=true;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            // 此处写你按返回键之后要执行的事件的逻辑
            SharedPreferences.Editor e = sp.edit();
            e.putString("text",ed.getText().toString());
            e.commit();
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
