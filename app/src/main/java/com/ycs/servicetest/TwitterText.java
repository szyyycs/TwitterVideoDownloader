package com.ycs.servicetest;

import cn.bmob.v3.BmobObject;

public class TwitterText extends BmobObject {
    private String filename;
    private String text;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
