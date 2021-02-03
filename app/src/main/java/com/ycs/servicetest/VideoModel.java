package com.ycs.servicetest;

import java.io.Serializable;

public class VideoModel implements Serializable {
    String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
