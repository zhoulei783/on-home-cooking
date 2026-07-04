package com.cooking.service.impl;
import java.util.Date;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.entity.Dish;
import com.cooking.entity.Order;
import com.cooking.entity.OrderItem;
import com.cooking.entity.Payment;
import com.cooking.entity.User;
import com.cooking.mapper.OrderMapper;
import com.cooking.service.*;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Resource
    private OrderItemService orderItemService;

    @Resource
    private PaymentService paymentService;

    @Resource
    private ChefService chefService;

    @Resource
    private UserService userService;

    @Resource
    private DishService dishService;

    @Resource
    private NotificationService notificationService;

    @Override
    public boolean createOrder(Order order) {
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        if (order.getStatus() == null) {
            order.setStatus(0);
        }
        if (order.getCreateTime() == null) {
            order.setCreateTime(new Date());
        }

        boolean orderSaved = this.save(order);

        if (orderSaved && order.getDishes() != null && !order.getDishes().isEmpty()) {
            for (OrderItem item : order.getDishes()) {
                item.setOrderId(order.getId());
                // 通过菜品ID获取菜品价格来计算小计
                if (item.getDishId() != null && item.getQuantity() != null) {
                    Dish dish = dishService.getById(item.getDishId());
                    if (dish != null) {
                        item.setSubtotal(dish.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    }
                }
                orderItemService.save(item);
            }
        }

        return orderSaved;
    }

    private String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String dateStr = sdf.format(new Date());
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(4);
        String random = String.format("%03d", new Random().nextInt(1000));
        return dateStr + timestamp + random;
    }

    private String generateTransactionId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.format("%04d", new Random().nextInt(10000));
        return "TX" + timestamp + random;
    }

    @Override
    public boolean updateOrderStatus(String orderNo, Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = this.getOne(wrapper);
        if (order != null) {
            order.setStatus(status);
            return this.updateById(order);
        }
        return false;
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<Order> getPaidOrdersByChefId(Long chefId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getChefId, chefId)
                .in(Order::getStatus, 2, 3, 4)
                .orderByDesc(Order::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<Order> getOrdersByChefId(Long chefId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getChefId, chefId)
                .orderByDesc(Order::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<Order> getValidOrders() {
        return this.list();
    }

    @Override
    public List<Order> getPaidAndCompletedOrders() {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Order::getStatus, 1, 2, 3, 4, 5, 6)
                .orderByDesc(Order::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public boolean payOrder(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = this.getOne(wrapper);

        if (order == null) {
            return false;
        }

        User user = userService.getById(order.getUserId());
        if (user == null) {
            return false;
        }

        if (user.getBalance().compareTo(order.getPrice()) < 0) {
            return false;
        }

        user.setBalance(user.getBalance().subtract(order.getPrice()));
        boolean userUpdated = userService.updateById(user);

        order.setStatus(1);
        boolean orderUpdated = this.updateById(order);

        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getPrice());
        payment.setStatus(1);
        payment.setPaymentTime(new Date());
        payment.setTransactionId(generateTransactionId());
        boolean paymentSaved = paymentService.save(payment);

        if (orderUpdated && paymentSaved) {
            notificationService.createNotification(
                    "admin",
                    0L,
                    order.getId(),
                    "新订单通知",
                    "用户支付成功，订单号：" + orderNo + "，金额：¥" + order.getPrice()
            );
        }

        return orderUpdated && paymentSaved;
    }

    @Override
    public Order getByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        return this.getOne(wrapper);
    }

    @Override
    public List<OrderItem> getItemsByOrderId(Long orderId) {
        return orderItemService.getItemsByOrderId(orderId);
    }

    /**
     * 用户取消服务中订单（变为取消订单且待审核状态）
     */
    @Override
    public boolean cancelOrderInService(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = this.getOne(wrapper);

        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        // 只能取消服务中的订单（状态2）
        if (order.getStatus() != 2) {
            throw new IllegalArgumentException("订单状态不允许取消");
        }

        // 更新为取消订单且待审核（状态6）
        order.setStatus(6);
        boolean updated = this.updateById(order);

        // 创建管理员通知
        if (updated) {
            notificationService.createNotification(
                    "admin",
                    0L,
                    order.getId(),
                    "订单取消申请",
                    "用户申请取消订单，订单号：" + orderNo
            );
        }

        return updated;
    }

    /**
     * 管理员审核取消订单（通过）
     */
    @Override
    public boolean approveCancelOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        // 只能审核取消订单且待审核的订单（状态6）
        if (order.getStatus() != 6) {
            throw new IllegalArgumentException("订单状态不正确");
        }

        // 查询用户，退还余额
        User user = userService.getById(order.getUserId());
        if (user != null) {
            if (user.getBalance() == null) {
                user.setBalance(BigDecimal.ZERO);
            }
            user.setBalance(user.getBalance().add(order.getPrice()));
            userService.updateById(user);
        }

        // 更新订单状态为已取消（状态5）
        order.setStatus(5);
        return this.updateById(order);
    }

    /**
     * 管理员审核取消订单（不通过）
     */
    @Override
    public boolean rejectCancelOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        // 只能审核取消订单且待审核的订单（状态6）
        if (order.getStatus() != 6) {
            throw new IllegalArgumentException("订单状态不正确");
        }

        // 更新订单状态为服务中（状态2）
        order.setStatus(2);
        return this.updateById(order);
    }
}