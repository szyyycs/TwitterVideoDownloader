package com.ycs.servicetest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.Field;

import moe.codeest.enviews.ENPlayView;

public class MyENPlayView extends ENPlayView {
    public static int DEFAULT_LINE_WIDTH = 2;

    public static int DEFAULT_BG_LINE_WIDTH = 2;
    public MyENPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        Field f= null;
//        try {
//            f = this.getClass().getField("DEFAULT_BG_LINE_WIDTH");
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//        f.setAccessible(true);
//        f.set(grade, "三年级一班");
    }
}
