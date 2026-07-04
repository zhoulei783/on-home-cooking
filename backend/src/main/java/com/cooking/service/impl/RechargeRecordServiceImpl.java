package com.cooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.entity.RechargeRecord;
import com.cooking.entity.User;
import com.cooking.mapper.RechargeRecordMapper;
import com.cooking.service.RechargeRecordService;
import com.cooking.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 充值记录服务实现类
 */
@Service
public class RechargeRecordServiceImpl extends ServiceImpl<RechargeRecordMapper, RechargeRecord> implements RechargeRecordService {

    @Resource
    @Lazy  // 添加延迟加载解决循环依赖
    private UserService userService;

    @Override
    @Transactional
    public boolean createRechargeRecord(Long userId, String userName, String userPhone, BigDecimal amount) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 创建充值记录，状态为待确认（0），不立即更新用户余额
        RechargeRecord record = new RechargeRecord();
        record.setUserId(userId);
        record.setAmount(amount);
        record.setStatus(0); // 0表示待确认
        record.setCreateTime(new Date());

        return this.save(record);
    }

    @Override
    public List<RechargeRecord> getAllRechargeRecords() {
        LambdaQueryWrapper<RechargeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(RechargeRecord::getCreateTime);
        List<RechargeRecord> records = this.list(wrapper);
        
        // 通过用户ID关联查询用户名称和电话
        for (RechargeRecord record : records) {
            if (record.getUserId() != null) {
                User user = userService.getById(record.getUserId());
                if (user != null) {
                    record.setUserName(user.getUsername());
                    record.setUserPhone(user.getPhone());
                }
            }
        }
        
        return records;
    }

    @Override
    public List<RechargeRecord> getRechargeRecordsByUserId(Long userId) {
        LambdaQueryWrapper<RechargeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RechargeRecord::getUserId, userId)
                .orderByDesc(RechargeRecord::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public boolean confirmRecharge(Long id) {
        RechargeRecord record = this.getById(id);
        if (record == null) {
            throw new IllegalArgumentException("充值记录不存在");
        }

        User user = userService.getById(record.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        BigDecimal currentBalance = user.getBalance();
        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }
        BigDecimal newBalance = currentBalance.add(record.getAmount());
        user.setBalance(newBalance);
        userService.updateById(user);

        record.setStatus(1);
        return this.updateById(record);
    }

    @Override
    @Transactional
    public boolean rejectRecharge(Long id) {
        RechargeRecord record = this.getById(id);
        if (record == null) {
            throw new IllegalArgumentException("充值记录不存在");
        }
        record.setStatus(2);
        return this.updateById(record);
    }
}
