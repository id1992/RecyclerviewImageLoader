package com.example.hhj.photogallery;

/**
 * Created by hhj on 16. 7. 13..
 */
public class PictureItem {
    int image;
    //String imagetitle;

    public int getImage() {
        return image;
    }

//    public String getImagetitle() {
//        return imagetitle;
//    }

    public PictureItem(int image, String imagetitle)
    {
        this.image=image;
        //this.imagetitle=imagetitle;
    }

}
