package com.ycs.servicetest;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.Serializable;

public class Items implements Serializable {
    private String text;
    private Bitmap src;
    private String size;
    private String time;
    private String video_len;
    private String url;
    private static final long serialVersionUID = 1L;
    public String getTwittertext() {
        return twittertext;
    }

    public void setTwittertext(String twittertext) {
        this.twittertext = twittertext;
    }

    private String twittertext;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVideo_len() {
        return video_len;
    }

    public void setVideo_len(String video_len) {
        this.video_len = video_len;
    }

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
