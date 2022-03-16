package com.ycs.smartcanteen.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.ycs.smartcanteen.R;
import androidx.core.content.ContextCompat;

public class StatusBarUtil {
    //隐藏状态栏
    public static void hideStatusBar(Activity app) {
        app.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        WindowManager.LayoutParams attrs = app.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        app.getWindow().setAttributes(attrs);
    }

    //全屏并且状态栏透明显示
    public static void showStatusBar(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
    }
    //设置状态栏颜色
    public static void setStatusBarColor(Activity activity) {
        Window window = activity.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.white));
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
