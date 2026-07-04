package com.cooking.service;
import com.cooking.entity.OrderItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.entity.Order;
import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     */
    boolean createOrder(Order order);

    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(String orderNo, Integer status);

    /**
     * 根据用户ID查询订单
     */
    List<Order> getOrdersByUserId(Long userId);

    /**
     * 根据厨师ID查询已支付订单
     */
    List<Order> getPaidOrdersByChefId(Long chefId);

    /**
     * 根据厨师ID查询订单
     */
    List<Order> getOrdersByChefId(Long chefId);

    /**
     * 获取所有有效订单
     */
    List<Order> getValidOrders();

    /**
     * 根据订单号查询订单
     */
    Order getByOrderNo(String orderNo);

    /**
     * 获取已支付和已完成的订单（管理员使用）
     */
    List<Order> getPaidAndCompletedOrders();

    /**
     * 支付订单（扣除用户余额）
     */
    boolean payOrder(String orderNo);

    List<OrderItem> getItemsByOrderId(Long orderId);

    /**
     * 用户取消服务中订单（变为取消订单且待审核状态）
     */
    boolean cancelOrderInService(String orderNo);

    /**
     * 管理员审核取消订单（通过）
     */
    boolean approveCancelOrder(Long orderId);

    /**
     * 管理员审核取消订单（不通过）
     */
    boolean rejectCancelOrder(Long orderId);
}