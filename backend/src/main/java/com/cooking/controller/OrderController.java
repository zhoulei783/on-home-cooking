package com.cooking.controller;

import com.cooking.common.Result;
import com.cooking.entity.Chef;
import com.cooking.entity.Order;
import com.cooking.entity.OrderItem;
import com.cooking.entity.Review;
import com.cooking.entity.User;
import com.cooking.service.ChefService;
import com.cooking.service.OrderItemService;
import com.cooking.service.OrderService;
import com.cooking.service.ReviewService;
import com.cooking.service.UserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "*")
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private OrderItemService orderItemService;

    @Resource
    private ChefService chefService;

    @Resource
    private UserService userService;

    @Resource
    private ReviewService reviewService;

    @PostMapping("/create")
    public Result<?> createOrder(@RequestBody Order order) {
        boolean success = orderService.createOrder(order);
        return success ? Result.success() : Result.fail("创建失败");
    }

    @PostMapping("/add")
    public Result<?> addOrder(@RequestBody Order order) {
        boolean success = orderService.createOrder(order);
        return success ? Result.success() : Result.fail("创建失败");
    }

    @GetMapping("/user/{userId}")
    public Result<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        for (Order order : orders) {
            List<OrderItem> items = orderItemService.getItemsByOrderId(order.getId());
            order.setDishes(items);

            if (order.getChefId() != null) {
                Chef chef = chefService.getById(order.getChefId());
                if (chef != null) {
                    order.setChefName(chef.getName());
                    order.setChefPhone(chef.getPhone());
                }
            }
        }
        return Result.success(orders);
    }

    @GetMapping("/chef/{chefId}")
    public Result<List<Order>> getOrdersByChefId(@PathVariable Long chefId) {
        List<Order> orders = orderService.getPaidOrdersByChefId(chefId);
        for (Order order : orders) {
            List<OrderItem> items = orderItemService.getItemsByOrderId(order.getId());
            order.setDishes(items);

            if (order.getUserId() != null) {
                User user = userService.getById(order.getUserId());
                if (user != null) {
                    order.setUsername(user.getUsername());
                    order.setUserPhone(user.getPhone());
                }
            }

            Review review = reviewService.getByOrderId(order.getId());
            if (review != null) {
                order.setRating(review.getRating());
                order.setComment(review.getContent());
            }
        }
        return Result.success(orders);
    }

    @GetMapping("/items/{orderId}")
    public Result<List<OrderItem>> getOrderItems(@PathVariable Long orderId) {
        List<OrderItem> items = orderItemService.getItemsByOrderId(orderId);
        return Result.success(items);
    }

    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order != null) {
            List<OrderItem> items = orderItemService.getItemsByOrderId(id);
            order.setDishes(items);

            if (order.getChefId() != null) {
                Chef chef = chefService.getById(order.getChefId());
                if (chef != null) {
                    order.setChefName(chef.getName());
                    order.setChefPhone(chef.getPhone());
                }
            }

            if (order.getUserId() != null) {
                User user = userService.getById(order.getUserId());
                if (user != null) {
                    order.setUsername(user.getUsername());
                    order.setUserPhone(user.getPhone());
                }
            }
        }
        return Result.success(order);
    }

    @PutMapping("/pay/{orderNo}")
    public Result<?> payOrder(@PathVariable String orderNo) {
        boolean success = orderService.payOrder(orderNo);
        return success ? Result.success() : Result.fail("支付失败：余额不足");
    }

    @PutMapping("/cancel/{orderNo}")
    public Result<?> cancelOrder(@PathVariable String orderNo) {
        try {
            Order order = orderService.getByOrderNo(orderNo);
            if (order == null) {
                return Result.fail("订单不存在");
            }

            if (order.getStatus() != 0) {
                return Result.fail("订单状态不允许取消");
            }

            order.setStatus(5);
            orderService.updateById(order);

            return Result.success();
        } catch (Exception e) {
            return Result.fail("取消失败：" + e.getMessage());
        }
    }

    /**
     * 用户取消服务中订单（变为取消订单且待审核状态）
     */
    @PutMapping("/cancel-in-service/{orderNo}")
    public Result<?> cancelOrderInService(@PathVariable String orderNo) {
        try {
            boolean success = orderService.cancelOrderInService(orderNo);
            return success ? Result.success() : Result.fail("取消失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/complete/{orderNo}")
    public Result<?> completeOrder(@PathVariable String orderNo) {
        try {
            Order order = orderService.getByOrderNo(orderNo);
            if (order == null) {
                return Result.fail("订单不存在");
            }

            if (order.getChefId() != null) {
                Chef chef = chefService.getById(order.getChefId());
                if (chef != null) {
                    BigDecimal earnings = order.getPrice().multiply(new BigDecimal("0.9"));

                    if (chef.getTotalEarnings() == null) {
                        chef.setTotalEarnings(BigDecimal.ZERO);
                    }
                    chef.setTotalEarnings(chef.getTotalEarnings().add(earnings));

                    if (chef.getRemainingAmount() == null) {
                        chef.setRemainingAmount(BigDecimal.ZERO);
                    }
                    chef.setRemainingAmount(chef.getRemainingAmount().add(earnings));

                    chefService.updateById(chef);
                }
            }

            order.setStatus(3);
            orderService.updateById(order);

            return Result.success();
        } catch (Exception e) {
            return Result.fail("操作失败：" + e.getMessage());
        }
    }
}