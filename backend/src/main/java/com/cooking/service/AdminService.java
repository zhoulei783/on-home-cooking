package com.cooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.entity.Admin;

/**
 * 管理员服务接口
 */
public interface AdminService extends IService<Admin> {

    /**
     * 根据手机号查询管理员
     * @param phone 手机号
     * @return 管理员信息
     */
    Admin getAdminByPhone(String phone);
}