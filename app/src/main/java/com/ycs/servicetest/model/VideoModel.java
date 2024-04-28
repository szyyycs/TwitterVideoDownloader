package com.ycs.servicetest.model;

import java.io.Serializable;

public class VideoModel implements Serializable {
    private String url;
    private String tweet;

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
