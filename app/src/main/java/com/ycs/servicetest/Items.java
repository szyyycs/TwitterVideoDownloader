package com.ycs.servicetest;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class Items {
    private String text;
    private Bitmap src;
    private String size;
    private String time;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Bitmap getSrc() {
        return src;
    }

    public void setSrc(Bitmap src) {
        this.src = src;
    }
}
