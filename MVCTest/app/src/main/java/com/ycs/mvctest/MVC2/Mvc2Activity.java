package com.ycs.mvctest.MVC2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ycs.mvctest.MVC.MainActivity;
import com.ycs.mvctest.R;

import org.jetbrains.annotations.NotNull;

public class Mvc2Activity extends AppCompatActivity{
    private EditText et_user;
    private EditText et_pwd;
    private Button bt_login;
    private UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvc2);
        userModel=new UserModel();
        initView();
    }

    private void initView() {
        et_user=findViewById(R.id.et_user);
        et_pwd=findViewById(R.id.et_pwd);
        bt_login=findViewById(R.id.bt_login);
        bt_login.setOnClickListener(v-> userModel.login(et_user.getText().toString(), et_pwd.getText().toString(),
                new OnLoginListener() {
                    @Override
                    public void onSuccess(@NotNull User user) {
                        Toast.makeText(Mvc2Activity.this, "欢迎" + user.getUser(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed() {
                        Toast.makeText(Mvc2Activity.this, "失败", Toast.LENGTH_SHORT).show();
                    }
                }));
    }
}