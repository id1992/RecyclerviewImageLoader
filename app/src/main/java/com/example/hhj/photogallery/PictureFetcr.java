package com.example.hhj.photogallery;

/**
 * Created by hhj on 16. 7. 14..
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**
 * Created by hhj on 16. 7. 14..
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/*******************************************************
 *
 *  함수명 : PictureFetchr
 *
 *  내용 : AsyncTask를 이용해 주어진 URL에서 이미지 src 부분만
 *  받아와 ArrayList 형태에 저장한다.
 *
 *******************************************************/
class PictureFetcr extends AsyncTask<ArrayList<String>,Void,ArrayList<String>>
{
    ArrayList<String> PhotoURL = new ArrayList<>();

    public PictureFetcr(ArrayList<String> PhotoURL) {
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

        ((MainActivity)MainActivity.mContext).setupAdapter();
    }
}