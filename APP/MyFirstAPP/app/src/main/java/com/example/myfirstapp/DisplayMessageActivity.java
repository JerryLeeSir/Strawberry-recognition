package com.example.myfirstapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.security.AccessController.getContext;

public class DisplayMessageActivity extends AppCompatActivity {
    protected static Bitmap imageBitmap;
    ImageView imageview;
    int flag = 1;
    Button button;
    File imagefile;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if(flag == 1) {
                Bitmap bitmap = (Bitmap) msg.obj;
                imageview.setImageBitmap(bitmap);//将图片的流转换成图片
            }else
            {
                flag = 1;
                imageview.setImageResource(R.drawable.o1);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_message);

        imageview = (ImageView)findViewById(R.id.imageview2);//初始化缩略图视图
        button = (Button)findViewById(R.id.button2);//初始化识别按钮

        imageview.setImageBitmap(imageBitmap);//展示缩略图

    }
    //将bitmap转化为png格式
    public File saveMyBitmap(Bitmap mBitmap){
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File file = null;
        try {
            file = File.createTempFile(
                    "00001",  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            FileOutputStream out=new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  file;
    }
    //上传按钮点击事件
    public  void identify(View view){
        button.setEnabled(false);//点击识别按钮后，不可再次点击
        button.setBackgroundResource(R.drawable.b3_1);
        Glide.with(this).load(R.drawable.loading).into(imageview);
        imagefile = saveMyBitmap(imageBitmap);//将bitmap转化为jpg格式
        uploading( "http://1.15.38.78:8000", imagefile);

    }
    //上传图片并返回识别结果
    private void uploading(String url, File file) {
        //超时设置


        //创建RequestBody封装参数
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), file);// MediaType.parse("image/jpeg")//application/octet-stream
        //创建MultipartBody,给RequestBody进行设置
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", "123456")
                .addFormDataPart("headImg",file.getName(), fileBody)
                .build();
        //创建Request
        Request request = new Request.Builder()
                .url(url)//"ip:1.15.38.78/www/wwwroot/DjangoFaster/myfaster/picture/data/demo"
                .post(multipartBody)
                .addHeader("Connection", "close")
                .build();

        //创建okhttp对象
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(60000, TimeUnit.SECONDS)
                .readTimeout(60000, TimeUnit.SECONDS)
                .writeTimeout(60000, TimeUnit.SECONDS)
                .build();

        //上传完图片,得到服务器反馈数据
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                imageview.setImageDrawable(getResources().getDrawable((R.drawable.w1)));
                Log.e("ffff", "uploadMultiFile() e=" + e);
                flag = 0;
                Message msg = new Message();
                msg.obj = R.drawable.o1;
                handler.sendMessage(msg);

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

//                Log.i("ff", "uploadMultiFile() response=" + response.body().string());
                flag = 1;
                InputStream inputStream = response.body().byteStream();//得到图片的流
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Message msg = new Message();
                msg.obj = bitmap;
                handler.sendMessage(msg);
            }
        });
    }


}