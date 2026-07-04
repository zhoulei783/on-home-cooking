package com.cooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.entity.Chef;
import java.util.List;
import java.util.Map;

/**
 * 厨师服务接口
 */
public interface ChefService extends IService<Chef> {
    /**
     * 根据手机号查询厨师
     */
    Chef getChefByPhone(String phone);

    /**
     * 根据擅长菜系查询厨师
     */
    List<Chef> getChefsBySkill(String skill);

    /**
     * 根据手机号和密码查询厨师
     */
    Chef getChefByPhoneAndPassword(String phone, String password);

    /**
     * 增加厨师收入
     */
    boolean addEarnings(Map<String, Object> map);

    /**
     * 发放厨师收入
     */
    boolean payEarnings(Map<String, Object> map);

    /**
     * 更新厨师收入信息
     */
    boolean updateEarnings(Map<String, Object> map);
}