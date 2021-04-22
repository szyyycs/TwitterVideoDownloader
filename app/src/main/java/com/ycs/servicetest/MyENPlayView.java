package com.ycs.servicetest;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import moe.codeest.enviews.ENPlayView;

public class MyENPlayView extends ENPlayView {



    public MyENPlayView(Context context) {
        super(context);
    }

    public MyENPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.DEFAULT_BG_LINE_WIDTH=3;
        super.DEFAULT_LINE_WIDTH=3;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

}
