package com.cooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.entity.User;
import com.cooking.mapper.UserMapper;
import com.cooking.service.UserService;
import com.cooking.service.RechargeRecordService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private RechargeRecordService rechargeRecordService;

    /**
     * 根据手机号查询用户
     */
    @Override
    public User getUserByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return this.getOne(wrapper);
    }

    /**
     * 用户注册（移除密码加密）
     */
    @Override
    public boolean register(User user) {
        // 检查手机号是否已存在
        if (getUserByPhone(user.getPhone()) != null) {
            throw new IllegalArgumentException("手机号已注册");
        }
        // 设置初始余额为0
        if (user.getBalance() == null) {
            user.setBalance(BigDecimal.ZERO);
        }
        return this.save(user);
    }

    /**
     * 用户充值（创建充值记录，等待管理员确认）
     */
    @Override
    public boolean recharge(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }
        User user = this.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 不直接更新余额，只创建充值记录（等待管理员确认）
        return rechargeRecordService.createRechargeRecord(userId, user.getUsername(), user.getPhone(), amount);
    }
}