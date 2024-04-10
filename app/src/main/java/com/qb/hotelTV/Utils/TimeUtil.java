package com.qb.hotelTV.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    // 获取当前日期和时间，并格式化为字符串
    public static String getCurrentDateTime() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}
