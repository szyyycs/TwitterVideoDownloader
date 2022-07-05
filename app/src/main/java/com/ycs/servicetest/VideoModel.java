package com.ycs.servicetest;

import java.io.Serializable;

public class VideoModel implements Serializable {
    private String url;

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    private String tweet;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
