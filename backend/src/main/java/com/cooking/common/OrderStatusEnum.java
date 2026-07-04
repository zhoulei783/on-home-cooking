package com.cooking.common;

import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
public enum OrderStatusEnum {
    PENDING_PAYMENT(0, "待支付"),
    PENDING_REVIEW(1, "待审核"),
    IN_SERVICE(2, "服务中"),
    PENDING_EVALUATION(3, "待评价"),
    COMPLETED(4, "已完成"),
    CANCELED(5, "已取消或审核不通过"),
    CANCEL_PENDING_REVIEW(6, "取消订单且待审核");

    private final Integer code;
    private final String desc;

    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 根据编码获取枚举
    public static OrderStatusEnum getByCode(Integer code) {
        for (OrderStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}