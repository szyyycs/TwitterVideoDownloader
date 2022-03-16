package com.ycs.smartcanteen.util;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

public class WindowsUtil {
    public static int getAppWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point.x;
    }
}
