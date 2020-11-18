package com.ycs.servicetest;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class Items {
    private String text;
    private Bitmap src;
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
