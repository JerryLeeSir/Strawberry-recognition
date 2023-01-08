package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_CHOOSE_PHOTO = 2;
    private static final int WRITE_PERMISSION = 0x01;
    ImageView imageview;
    Uri photoURI;
    String currentPhotoPath;
    Button buttonchoic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题栏
        getSupportActionBar().hide();
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
//        imageview = (ImageView)findViewById(R.id.imageview);
        buttonchoic = (Button)findViewById(R.id.button3);
        requestWritePermission();

    }
    private void requestWritePermission()
    {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
        }
    }
    /** 调用相机拍照 */
    //调用相机
    public void dispatchTakePictureIntent(View view) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
//                System.out.println("---------------------------------------22222222222222222");
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.myfirstapp.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }else
        {
            System.out.println("---------------------------------------");
        }
    }
    //旋转Bitmap
    Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {

        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);

        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);

            return bm1;

        } catch (OutOfMemoryError ex) {
        }
        return null;

    }
    //展示缩略图函数在最下方
    //保存图片
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
//        setPic();
        galleryAddPic();
        return image;
    }
    //存入图库
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    //图片解码
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageview.getWidth();
        int targetH = imageview.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageview.setImageBitmap(bitmap);
    }

    /** 调用相册选择图片*/
    //相册点击事件
    public void get(View view){
        openSysAlbum();
    }

    //打开系统相册,定义Intent跳转到特定图库的Uri下挑选，然后将挑选结果返回给Activity
    private void openSysAlbum() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, REQUEST_CHOOSE_PHOTO);
    }
    //这部分的代码目前没有理解，只知道作用是根据条件的不同去获取相册中图片的url,这一部分是从其他博客中查询的
    @TargetApi(value = 19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        // 根据图片路径显示图片

        displayImage(imagePath);
        System.out.println(imagePath);
    }
    //获取图片的路径
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){

            if(cursor.moveToFirst()){
                path = cursor.getString((cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            }
            cursor.close();
        }
        return path;
    }
    //展示图片
    private void displayImage(String imagePath) {
        Intent intent2 = new Intent();
        intent2.setClass(MainActivity.this, DisplayMessageActivity.class);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//        adjustPhotoRotation(bitmap, 90);
        DisplayMessageActivity.imageBitmap = bitmap;
        startActivity(intent2);
//        imageview.setImageBitmap(bitmap);
    }


    //展示缩略图
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, DisplayMessageActivity.class);
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                    try {

                        Bitmap imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(photoURI));
                        imageBitmap = adjustPhotoRotation(imageBitmap, 90);
                        DisplayMessageActivity.imageBitmap = imageBitmap;
                        startActivity(intent);
                        //                imageview.setImageBitmap(imageBitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //            Bundle extras = data.getExtras();
                    //            Bitmap imageBitmap = (Bitmap) extras.get("data");
                    //            imageView.setImageBitmap(imageBitmap);
                }
                break;
            case REQUEST_CHOOSE_PHOTO:
                handleImageOnKitKat(data);
                break;
        }
    }

    /**项目简介按钮点击事件**/
    public void IntroduceOnClick(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, introduce.class);
        startActivity(intent);
    }
}