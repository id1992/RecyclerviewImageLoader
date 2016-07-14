package com.example.hhj.photogallery;

/**
 * Created by hhj on 16. 7. 14..
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/*******************************************************
 *
 *  함수명 : BitmapWorkerTask (AsyncTask)
 *
 *  내용 : 주어진 URL을 이용해 비트맵을 생성하는 함수
 *
 *******************************************************/

//class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
//    private WeakReference<ImageView> imageViewReference;
//    Bitmap bm;
//    RecyclerView.ViewHolder holder;
//    String srcurl;
//    int position;
//    ImageView preImage;
//
//    public BitmapWorkerTask(ImageView imageView, String url, RecyclerView.ViewHolder holder, int position) {
//        // WeakReference를 사용한 이유는 일반 참조를 쓰게 되면 GC대상에서 제외되 메모리 릭을 유발할 수 있기 때문
//        imageViewReference = new WeakReference<>(imageView);
//        this.srcurl = url;
//        this.holder = holder;
//        this.position = position;
//        this.preImage = imageView;
//    }
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//
//        preImage.setImageResource(R.drawable.loading);
//    }
//
//    // 이미지를 Background에서 Decode
//    @Override
//    protected Bitmap doInBackground(String... params) {
//        try{
//            URL url = new URL(srcurl);
//
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setDoInput(true);
//            conn.connect();
//
//            InputStream is = conn.getInputStream();
//            bm = BitmapFactory.decodeStream(is);
//
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // 받아온 비트맵을 캐싱해줌
//        ((MainActivity)MainActivity.mContext).addBitmapToMemoryCache(Integer.toString(position), bm);
//
//        return bm;
//    }
//
//    // 이미지,
//    @Override
//    protected void onPostExecute(Bitmap bm) {
//        if (imageViewReference != null && bm != null) {
//            final ImageView imageView = imageViewReference.get();
//            if (imageView != null) {
//                imageView.setImageBitmap(bm);
//                imageViewReference = new WeakReference<>(imageView);
//            }
//        }
//    }
//}

/*******************************************************
 *
 *  함수명 : BitmapWorkerTask (Thread, Handler)
 *
 *  내용 : 주어진 URL을 이용해 비트맵을 생성하는 함수
 *
 *******************************************************/

class BitmapWorkerTask extends Thread
{
    private WeakReference<ImageView> imageViewReference;
    Bitmap bm;
    RecyclerView.ViewHolder holder;
    String srcurl;
    int position;
    ImageView preImage;
    final Handler handler = new Handler();

    public BitmapWorkerTask(ImageView imageView, String url, RecyclerView.ViewHolder holder, int position) {
        // WeakReference를 사용한 이유는 일반 참조를 쓰게 되면 GC대상에서 제외되 메모리 릭을 유발할 수 있기 때문
        imageViewReference = new WeakReference<>(imageView);
        this.srcurl = url;
        this.holder = holder;
        this.position = position;
        this.preImage = imageView;
    }

    public void run()
    {
        try{
            URL url = new URL(srcurl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            bm = BitmapFactory.decodeStream(is);


        }catch (Exception e) {
            e.printStackTrace();
        }

        // 받아온 비트맵을 캐싱해줌
        ((MainActivity)MainActivity.mContext).addBitmapToMemoryCache(Integer.toString(position), bm);


        handler.post(new Runnable(){
            @Override
            public void run() {  // Runnable 의 Run() 메소드에서 UI 접근
                if (imageViewReference != null && bm != null) {

                    final ImageView imageView = imageViewReference.get();

                    if (imageView != null) {
                        imageView.setImageBitmap(bm);
                        imageViewReference = new WeakReference<>(imageView);

                    }
                }
            }
        });

        //handler.sendEmptyMessage(1);
    }

//    final Handler handler = new Handler(){
//
//        @Override
//        public void handleMessage(Message msg){
//
//            if (imageViewReference != null && bm != null) {
//
//                final ImageView imageView = imageViewReference.get();
//
//                if (imageView != null) {
//                imageView.setImageBitmap(bm);
//                imageViewReference = new WeakReference<>(imageView);
//
//                }
//            }
//        }
//    };
}

