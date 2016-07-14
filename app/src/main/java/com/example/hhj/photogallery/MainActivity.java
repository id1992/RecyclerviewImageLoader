package com.example.hhj.photogallery;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static Context mContext;

    private RecyclerView mPhotoRecyclerView;
    private RecyclerView.Adapter PictureAdapter;

    // 이미지 태그의 Src 부분 저장 변수
    ArrayList<String> PhotoURL;

    // 캐싱을 위한 변수
    private LruCache<String, Bitmap> mMemoryCache;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        // Lru Cache 동작을 위해 캐시메모리 세팅
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // 사용가능한 메모리의 1/2를 캐시메모리로 사용함
        final int cacheSize = maxMemory / 2;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };

        mPhotoRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // 파싱된 URL을 저장할 ArrayList
        PhotoURL = new ArrayList<>();

        // 리스트에 데이터 삽입
        PictureFetcr pictureFetcr = new PictureFetcr(PhotoURL);
        pictureFetcr.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null);

        // GridLayoutManager 사용
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(this,3));

    }


    /*******************************************************
     *
     *  함수명 : addBitmapToMemoryCache
     *
     *  내용 : 비트맵을 Key 값에 할당하는 함수
     *
     *******************************************************/
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }


    /*******************************************************
     *
     *  함수명 : getBitmapFromMemCache
     *
     *  내용 : Key 값으로 비트맵을 읽어오는 함
     *
     *******************************************************/
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


    /*******************************************************
     *
     *  함수명 : setupAdapter
     *
     *  내용 : Adapter를 생성하고 RecycleView에 지정하는 함수
     *
     *******************************************************/
    public void setupAdapter()
    {
        // Adapter 생성
        PictureAdapter = new PictureAdapter(PhotoURL);
        mPhotoRecyclerView.setAdapter(PictureAdapter);
    }

}
