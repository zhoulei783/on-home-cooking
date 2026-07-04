package com.cooking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cooking.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; // 添加这个import
import java.util.List;
import java.util.Map;

/**
 * 用户Mapper接口（关联UserMapper.xml）
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据手机号查询用户（对应XML中的selectByPhone）
     * @param phone 手机号
     * @return 用户信息
     */
    User selectByPhone(String phone);

    /**
     * 根据ID更新密码（对应XML中的updatePasswordById）
     * @param map 包含userId和password的参数Map
     * @return 受影响行数
     */
    int updatePasswordById(@Param("map") Map<String, Object> map);

    /**
     * 分页查询用户（支持用户名模糊查询，对应XML中的selectUserPage）
     * @param map 包含分页参数和查询条件的Map
     * @return 用户列表
     */
    List<User> selectUserPage(@Param("map") Map<String, Object> map);

    /**
     * 更新余额（充值）
     * @param map 包含userId和amount的参数Map
     * @return 受影响行数
     */
    int updateBalance(Map<String, Object> map);
}