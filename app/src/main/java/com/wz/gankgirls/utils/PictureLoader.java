package com.wz.gankgirls.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PictureLoader {
    private ImageView loadImg;
    private String imgUrl;
    private byte[] picByte;

   Handler mHandler = new Handler(){
       @Override
       public void handleMessage(@NonNull Message msg) {
           super.handleMessage(msg);
           //收到更新允许
           if (msg.what == 0x123){
               if (picByte != null){
                   Bitmap bitmap = BitmapFactory.decodeByteArray(picByte, 0, picByte.length);
                   //更新视图
                   loadImg.setImageBitmap(bitmap);
               }
           }
       }
   };
    public void load(ImageView loadImg, String imgUrl) {
        //传入布局和图片资源地址
        this.loadImg = loadImg;
        this.imgUrl = imgUrl;
        //检查View中此时有没有图 进行释放
        Drawable drawable = loadImg.getDrawable();


        if(drawable != null && drawable instanceof BitmapDrawable) {
            //转换Bitmap位图
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            if(bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        new Thread(runnable).start();
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(imgUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int length = -1;
                    while ((length = in.read(bytes)) != -1) {
                        out.write(bytes, 0, length);
                    }
                    //得到图片资源
                    picByte = out.toByteArray();
                    in.close();
                    out.close();
                    //发送刷新请求
                    mHandler.sendEmptyMessage(0x123);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
