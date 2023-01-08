package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class introduce extends AppCompatActivity {
    Button button;
//    int PressFlag = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        button = (Button)findViewById(R.id.button5);
        //取消标题栏
        getSupportActionBar().hide();
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
    }
    //屏幕按钮点击事件
    public void GoButtonOnClick(View view) {

//        if(PressFlag == 1){
////            view.getLayoutParams().width = 138;
//            view.setBackground(getResources().getDrawable(R.drawable.b1));
//            PressFlag = 0;
//            System.out.println(view.getLayoutParams().width);
//        }else{
////            view.getLayoutParams().width = 138;
//            view.setBackground(getResources().getDrawable(R.drawable.b5));
//            PressFlag = 1;
//            System.out.println(view.getLayoutParams().width);
//        }
        Intent intent = new Intent();
        intent.setClass(introduce.this, ChengYuan.class);
        startActivity(intent);
    }
    //返回按钮事件
    public void Button_Black(View view){
        Intent intent = new Intent();
        intent.setClass(introduce.this, MainActivity.class);
        startActivity(intent);
    }
}

