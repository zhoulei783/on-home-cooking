package com.cooking.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;

/**
 * 订单编号生成工具
 */
public class OrderNoGenerator {

    /**
     * 生成订单编号：时间戳（yyyyMMddHHmmss）+ 6位随机数
     */
    public static String generateOrderNo() {
        String dateStr = DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss");
        String randomStr = RandomUtil.randomNumbers(6);
        return dateStr + randomStr;
    }
}