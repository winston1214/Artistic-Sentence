package org.techtown.capston_sample_1;

import android.widget.ImageView;

public class Style {
    int image;
    String name;

    public Style(int image, String name) {
        this.image = image;
        this.name = name;
    }

    public int getImage(){
        return image;
    }

    public void setImage(int resId){
        this.image = resId;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
}
