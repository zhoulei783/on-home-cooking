package com.cooking.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户余额流水。
 */
@Data
public class BalanceTransactionDTO {
    private String id;
    private String type;
    private String title;
    private String description;
    private BigDecimal amount;
    private BigDecimal changeAmount;
    private BigDecimal balanceAfter;
    private String status;
    private Date createTime;
    private String orderNo;
    private String transactionId;
}
