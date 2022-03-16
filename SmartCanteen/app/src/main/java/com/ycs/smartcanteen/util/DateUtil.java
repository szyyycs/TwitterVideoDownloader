package com.ycs.smartcanteen.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String getWeek(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("E");
        String week = sdf.format(date);
        return week;
    }
}
