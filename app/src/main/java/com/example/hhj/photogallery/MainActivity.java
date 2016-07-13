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

    Context mContext;

    private RecyclerView mPhotoRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<String> PhotoURL;

    private LruCache<String, Bitmap> mMemoryCache;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // 사용가능한 메모리의 1/2를 캐시메모리로 사용함
        final int cacheSize = maxMemory / 2;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        mPhotoRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        // 파싱된 URL을 저장할 ArrayList
        PhotoURL = new ArrayList<>();

        PictureFetchr pictureFetchr = new PictureFetchr(PhotoURL);
        pictureFetchr.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null);
        // PictureItem 리스트에 데이터 삽입

        // GridLayoutManager 사용
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(this,3));



    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

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
    private void setupAdapter()
    {
        // Adapter 생성
        mAdapter = new mAdapter(PhotoURL);
        mPhotoRecyclerView.setAdapter(mAdapter);
    }


    /*******************************************************
     *
     *  함수명 : mAdapter
     *
     *  내용 : RecyclerView를 관리하는 Adapter
     *
     *******************************************************/
    class mAdapter extends RecyclerView.Adapter<mAdapter.ViewHolder>{
        ArrayList<String> PhotoURL = new ArrayList<>();

        public mAdapter(ArrayList<String> PhotoURL) {
            super();

            this.PhotoURL = PhotoURL;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // 새로운 뷰를 만듦
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);
            ViewHolder holder = new ViewHolder(v);

            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            // 현재 포지션을 키 값으로 지정한다.
            final String imageKey = String.valueOf(position);

            // 키 값에 대응하는 비트맵을 가져온다
            final Bitmap bitmap = getBitmapFromMemCache(imageKey);

            // 키 값에 대응하는 비트맵이 있으면 비트맵을 set 해주고 없으면 비트맵을 받아온다.
            if(bitmap != null)
            {
                holder.imageView.setImageBitmap(bitmap);
            }
            else {
                Log.e("position", position + "");
                Log.e("PhotoURL", PhotoURL.get(position));
                BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(holder.imageView, PhotoURL.get(position), holder, position);
                bitmapWorkerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            }
        }

        @Override
        public int getItemCount() {
            return PhotoURL.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public ImageView imageView;

            public ViewHolder(View view){
                super(view);
                imageView = (ImageView) view.findViewById(R.id.imageView);
            }
        }

    }


    /*******************************************************
     *
     *  함수명 : PictureFetchr
     *
     *  내용 : AsyncTask를 이용해 주어진 URL에서 이미지 src 부분만
     *  받아와 ArrayList 형태에 저장한다.
     *
     *******************************************************/
    private class PictureFetchr extends AsyncTask<ArrayList<String>,Void,ArrayList<String>>
    {
        ArrayList<String> PhotoURL = new ArrayList<>();

        public PictureFetchr(ArrayList<String> PhotoURL) {
            super();

            this.PhotoURL = PhotoURL;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... params) {

            // 이미지 태그 앞에 붙는 기본 주소
            String baseAddress = "http://www.gettyimagesgallery.com";

            try {

                URL url = new URL("http://www.gettyimagesgallery.com/collections/archive/slim-aarons.aspx");
                URLConnection conn = url.openConnection();

                // 소스코드 가져오기 위해 스트림 선언
                BufferedReader br;
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line;
                int linenum = 0;


                while((line=br.readLine())!=null)
                {
                    linenum++;
                    // 불러오려는 이미지 line num은 230
                    if(linenum > 230) {
                        if (line.contains("<img")) {
                            String str[] = line.split("<img src=\"");
                            String path[] = str[1].split("\"");
                            if (path[0].contains("Thumbnails")) {
                                PhotoURL.add(baseAddress + path[0]);
                            }
                        }
                    }

                }
            } catch (Exception e) {
                // TODO 자동 생성된 catch 블록
                e.printStackTrace();
            }

            return PhotoURL;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            for(int i = 0;i < PhotoURL.size(); i++)
            Log.e("추출된 URL : ", PhotoURL.get(i));

            setupAdapter();
        }
    }

    /*******************************************************
     *
     *  함수명 : BitmapWorkerTask
     *
     *  내용 : 주어진 URL을 이용해 비트맵을 생성하는 함수
     *
     *******************************************************/

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private WeakReference<ImageView> imageViewReference;
        Bitmap bm;
        RecyclerView.ViewHolder holder;
        String srcurl;
        int position;
        ImageView preImage;

        public BitmapWorkerTask(ImageView imageView, String url, RecyclerView.ViewHolder holder, int position) {
            // WeakReference를 사용한 이유는 일반 참조를 쓰게 되면 GC대상에서 제외되 메모리 릭을 유발할 수 있기 때문
            imageViewReference = new WeakReference<>(imageView);
            this.srcurl = url;
            this.holder = holder;
            this.position = position;
            this.preImage = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            preImage.setImageResource(R.drawable.loading);
        }

        // 이미지를 Background에서 Decode
        @Override
        protected Bitmap doInBackground(String... params) {
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
            addBitmapToMemoryCache(Integer.toString(position), bm);

            return bm;
        }

        // 이미지,
        @Override
        protected void onPostExecute(Bitmap bm) {
            if (imageViewReference != null && bm != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bm);
                    imageViewReference = new WeakReference<>(imageView);
                }
            }
        }
    }
}
