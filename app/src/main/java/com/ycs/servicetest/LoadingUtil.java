package com.ycs.servicetest;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

public class LoadingUtil {
    private static Dialog dialog;
    public static void init(Context context){
        if(dialog==null||!dialog.isShowing()){
            AVLoadingIndicatorView view = new AVLoadingIndicatorView(context);
            view.setIndicator("PacmanIndicator");

            view.setIndicatorColor(Color.parseColor("#0b989d"));
            LinearLayout ll = new LinearLayout(context);

            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setBackgroundResource(R.drawable.shape_fff);
            ll.setGravity(Gravity.CENTER);
            ll.addView(view,new LinearLayout.LayoutParams(200,200));
            TextView tv = new TextView(context);
            tv.setTextColor(Color.parseColor("#444444"));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
            tv.setText("加载中,请稍后...");
            ll.addView(tv,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            dialog = new Dialog(context);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 1f;
            window.setAttributes(lp);
            dialog.setCancelable(false);
            dialog.setContentView(ll,new LinearLayout.LayoutParams(400,400));// 设置布局

        }
    }
    public static void Loading_show(Context context) {
        init(context);
        dialog.show();
    }

    public static void Loading_close() {
        if(dialog!=null){dialog.cancel();}
    }
}