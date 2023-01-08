package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class ChengYuan extends AppCompatActivity {
    int flag1 = 1, flag2 = 1, flag3 = 1, flag4 = 1, flag5 = 1, flag6 = 1;
    Button btn1, btn2, btn3, btn4, btn5, btn6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //取消标题栏
        getSupportActionBar().hide();
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheng_yuan);

        btn1 = (Button)findViewById(R.id.button6);
        btn2 = (Button)findViewById(R.id.button7);
        btn3 = (Button)findViewById(R.id.button8);
        btn4 = (Button)findViewById(R.id.button9);
        btn5 = (Button)findViewById(R.id.button10);
        btn6 = (Button)findViewById(R.id.button11);

    }
    //点击某个按钮时，其它按钮回归正常
    private void BalckOringin(int n){
        if (flag1 == 0 && n != 1){
            btn1.getLayoutParams().width = 978;
            btn1.getLayoutParams().height = 173;
            flag1 = 1;
            btn1.setBackground(getResources().getDrawable(R.drawable.l2));
        }
        if (flag2 == 0 && n != 2){
            btn2.getLayoutParams().width = 978;
            btn2.getLayoutParams().height = 173;
            flag2 = 1;
            btn2.setBackground(getResources().getDrawable(R.drawable.z2));
        }
        if (flag3 == 0 && n != 3){
            btn3.getLayoutParams().width = 978;
            btn3.getLayoutParams().height = 173;
            flag3 = 1;
            btn3.setBackground(getResources().getDrawable(R.drawable.h2));
        }
        if (flag4 == 0 && n != 4){
            btn4.getLayoutParams().width = 978;
            btn4.getLayoutParams().height = 173;
            flag4 = 1;
            btn4.setBackground(getResources().getDrawable(R.drawable.y2));
        }
        if (flag5 == 0 && n != 5){
            btn5.getLayoutParams().width = 978;
            btn5.getLayoutParams().height = 173;
            flag5 = 1;
            btn5.setBackground(getResources().getDrawable(R.drawable.r2));
        }
        if (flag6 == 0 && n != 6){
            btn6.getLayoutParams().width = 978;
            btn6.getLayoutParams().height = 173;
            flag6 = 1;
            btn6.setBackground(getResources().getDrawable(R.drawable.w2));
        }
    }

    //下面是6个按钮的点击事件
    public void OnclickBtn1(View view){
        if (flag1 == 1) {
            System.out.println(view.getLayoutParams().width);
            System.out.println(view.getLayoutParams().height);
            view.getLayoutParams().width = 973;
            view.getLayoutParams().height = 242;
            view.setBackground(getResources().getDrawable(R.drawable.l1));
            flag1 = 0;
            BalckOringin(1);
        }
    }
    public void OnclickBtn2(View view){
        view.getLayoutParams().width = 973;
        view.getLayoutParams().height = 242;
        view.setBackground(getResources().getDrawable(R.drawable.z1));
        flag2 = 0;
        BalckOringin(2);
    }
    public void OnclickBtn3(View view){
        view.getLayoutParams().width = 973;
        view.getLayoutParams().height = 242;
        view.setBackground(getResources().getDrawable(R.drawable.h1));
        flag3 = 0;
        BalckOringin(3);
    }
    public void OnclickBtn4(View view){
        view.getLayoutParams().width = 973;
        view.getLayoutParams().height = 242;
        view.setBackground(getResources().getDrawable(R.drawable.y1));
        flag4 = 0;
        BalckOringin(4);
    }
    public void OnclickBtn5(View view){
        view.getLayoutParams().width = 973;
        view.getLayoutParams().height = 242;
        view.setBackground(getResources().getDrawable(R.drawable.r1));
        flag5 = 0;
        BalckOringin(5);
    }
    public void OnclickBtn6(View view){
        view.getLayoutParams().width = 973;
        view.getLayoutParams().height = 242;
        view.setBackground(getResources().getDrawable(R.drawable.w1));
        flag6 = 0;
        BalckOringin(6);
    }

    //导航栏中间按钮点击事件
    public void OnclickBtnM(View view){
        Intent intent = new Intent();
        intent.setClass(ChengYuan.this, ShiYan.class);
        startActivity(intent);
    }
    //导航栏右侧按钮点击事件
    public void OnclickBtnR(View view){
        Intent intent = new Intent();
        intent.setClass(ChengYuan.this, setting.class);
        startActivity(intent);
    }
}

