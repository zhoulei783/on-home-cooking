package com.cooking.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户余额总览及流水明细。
 */
@Data
public class BalanceDetailsDTO {
    private BigDecimal balance = BigDecimal.ZERO;
    private BigDecimal totalIncome = BigDecimal.ZERO;
    private BigDecimal totalExpense = BigDecimal.ZERO;
    private List<BalanceTransactionDTO> transactions = new ArrayList<>();
}
