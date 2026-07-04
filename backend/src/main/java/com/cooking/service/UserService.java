package com.cooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    /**
     * 根据手机号查询用户
     */
    User getUserByPhone(String phone);

    /**
     * 用户注册
     */
    boolean register(User user);

    /**
     * 用户充值
     * @param userId 用户ID
     * @param amount 充值金额
     * @return 是否成功
     */
    boolean recharge(Long userId, java.math.BigDecimal amount);
}