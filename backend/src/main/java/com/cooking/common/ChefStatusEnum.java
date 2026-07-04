package com.cooking.common;

import lombok.Getter;

/**
 * 厨师状态枚举
 */
@Getter
public enum ChefStatusEnum {
    IDLE(0, "空闲"),
    BUSY(1, "忙碌");

    private final Integer code;
    private final String desc;

    ChefStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 根据编码获取枚举
    public static ChefStatusEnum getByCode(Integer code) {
        for (ChefStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}