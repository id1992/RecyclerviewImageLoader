package com.example.hhj.photogallery;

/**
 * Created by hhj on 16. 7. 14..
 */

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/*******************************************************
 *
 *  함수명 : PictureAdapter
 *
 *  내용 : RecyclerView를 관리하는 Adapter
 *
 *******************************************************/
class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder>{
    ArrayList<String> PhotoURL = new ArrayList<>();

    public PictureAdapter(ArrayList<String> PhotoURL) {
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
        final Bitmap bitmap = ((MainActivity)MainActivity.mContext).getBitmapFromMemCache(imageKey);

        // 키 값에 대응하는 비트맵이 있으면 비트맵을 set 해주고 없으면 비트맵을 받아온다.
        if(bitmap != null)
        {
            holder.imageView.setImageBitmap(bitmap);
        }
        else {
            Log.e("position", position + "");
            Log.e("PhotoURL", PhotoURL.get(position));

            /* AsyncTask 동작
            BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(holder.imageView, PhotoURL.get(position), holder, position);
            bitmapWorkerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            */

            holder.imageView.setImageResource(R.drawable.loading);

            BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(holder.imageView, PhotoURL.get(position), holder, position);
            bitmapWorkerTask.start();


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
