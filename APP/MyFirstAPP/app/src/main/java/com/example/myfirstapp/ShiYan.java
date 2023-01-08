package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class ShiYan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //取消标题栏
        getSupportActionBar().hide();
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shi_yan);
    }

    //返回按钮
    public void Button_Black(View view){
        Intent intent = new Intent();
        intent.setClass(ShiYan.this, introduce.class);
        startActivity(intent);
    }
    //成员页面按钮
    public void Button_Member(View view){
        Intent intent = new Intent();
        intent.setClass(ShiYan.this, ChengYuan.class);
        startActivity(intent);
    }
    //设置界面按钮
    public void Button_Setting(View view){
        Intent intent = new Intent();
        intent.setClass(ShiYan.this, setting.class);
        startActivity(intent);
    }
}