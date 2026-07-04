package com.cooking.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import java.util.Date;

/**
 * 日期工具类（封装Hutool）
 */
public class CustomDateUtil extends DateUtil {

    /**
     * 日期转字符串（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        return DateUtil.format(date, DatePattern.NORM_DATETIME_PATTERN);
    }

    /**
     * 字符串转日期（yyyy-MM-dd HH:mm:ss）
     */
    public static Date parseDateTime(String dateStr) {
        return DateUtil.parse(dateStr, DatePattern.NORM_DATETIME_PATTERN);
    }

    /**
     * 判断日期是否在未来
     */
    public static boolean isFuture(Date date) {
        return date.after(new Date());
    }
}