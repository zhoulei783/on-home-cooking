package com.cooking.common;

import lombok.Getter;

/**
 * 厨师等级枚举
 * 中国厨师证分为五个等级：初级、中级、高级、技师、高级技师
 */
@Getter
public enum ChefLevelEnum {
    JUNIOR(1, "初级"),
    INTERMEDIATE(2, "中级"),
    SENIOR(3, "高级"),
    TECHNICIAN(4, "技师"),
    SENIOR_TECHNICIAN(5, "高级技师");

    private final Integer code;
    private final String desc;

    ChefLevelEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据编码获取枚举
     */
    public static ChefLevelEnum getByCode(Integer code) {
        for (ChefLevelEnum level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return null;
    }

    /**
     * 根据描述获取枚举
     */
    public static ChefLevelEnum getByDesc(String desc) {
        for (ChefLevelEnum level : values()) {
            if (level.getDesc().equals(desc)) {
                return level;
            }
        }
        return null;
    }
}