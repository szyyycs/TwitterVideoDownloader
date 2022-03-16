package com.ycs.smartcanteen.util;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by  HONGDA on 2018/6/15.
 */
public class MyViewPager extends ViewPager {
    private int current;
    private HashMap<Integer, View> mMap = new LinkedHashMap<>();
    private int height = 0;
    //是否可以进行滑动
    private boolean canScroll = true;//默认可以滑动

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int height = 0;
//        for (int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//            int h = child.getMeasuredHeight();
//            mMap.put(i,h);
//            if (h > height)height = h;
//        }

        if (mMap.size() > current) {
            View child = getChildAt(current);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            height = child.getMeasuredHeight();
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    public void resetHeight(int current) {
        this.current = current;
        if (mMap.size() > current) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            } else {
                layoutParams.height = height;
            }
            setLayoutParams(layoutParams);
        }
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }
    public void setObjectForPosition(View view, int position) {
        mMap.put(position, view);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        if (!canScroll) {
//            return true;
//        }
//        return super.onTouchEvent(ev);
//    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean inter=super.onInterceptTouchEvent(ev);

        float lastX=0;
        float lastY=0;
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX=ev.getX();
                lastY=ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float x=Math.abs(ev.getX()-lastX);
                float y=Math.abs(ev.getY()-lastY);
//                Log.d("yyy", "x:"+x+"y:"+y);
//                if((x>10||y>10)&&x>=y) {
//                    Log.d("yyy", "true--"+"x:"+x+"y:"+y);
//                    requestDisallowInterceptTouchEvent(true);
//                    return true;
//                }

                break;
            case MotionEvent.ACTION_UP:
                return false;

        }
        return inter;
        //return canScroll;
    }
}


