package com.cooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.entity.RechargeRecord;
import java.math.BigDecimal;
import java.util.List;

public interface RechargeRecordService extends IService<RechargeRecord> {
    boolean createRechargeRecord(Long userId, String userName, String userPhone, BigDecimal amount);
    List<RechargeRecord> getAllRechargeRecords();
    List<RechargeRecord> getRechargeRecordsByUserId(Long userId);
    boolean confirmRecharge(Long id);
    boolean rejectRecharge(Long id);
}
