package com.cooking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cooking.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

/**
 * 订单Mapper接口（关联OrderMapper.xml）
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据订单编号查询订单（对应XML中的selectByOrderNo）
     * @param orderNo 订单编号
     * @return 订单信息
     */
    Order selectByOrderNo(String orderNo);

    /**
     * 根据用户ID查询订单（对应XML中的selectByUserId）
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> selectByUserId(Long userId);

    /**
     * 根据厨师ID查询订单（对应XML中的selectByChefId）
     * @param chefId 厨师ID
     * @return 订单列表
     */
    List<Order> selectByChefId(Long chefId);

    /**
     * 根据订单编号更新状态（对应XML中的updateStatusByOrderNo）
     * @param map 包含orderNo和status的参数Map
     * @return 受影响行数
     */
    int updateStatusByOrderNo(@Param("map") Map<String, Object> map);

    /**
     * 分页查询订单（支持用户ID/厨师ID/状态筛选，对应XML中的selectOrderPage）
     * @param map 包含分页参数和查询条件的Map
     * @return 订单列表
     */
    List<Order> selectOrderPage(@Param("map") Map<String, Object> map);
}