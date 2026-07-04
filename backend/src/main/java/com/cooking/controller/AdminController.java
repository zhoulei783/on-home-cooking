package com.cooking.controller;

import com.cooking.common.Result;
import com.cooking.entity.*;
import com.cooking.service.*;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Resource
    private OrderService orderService;

    @Resource
    private ChefService chefService;

    @Resource
    private UserService userService;

    @Resource
    private DishService dishService;

    @Resource
    private RechargeRecordService rechargeRecordService;

    @Resource
    private OrderItemService orderItemService;

    @Resource
    private NotificationService notificationService;

    @GetMapping("/orders")
    public Result<List<Order>> getAllOrders() {
        List<Order> orders = orderService.list();
        return Result.success(fillOrderDetails(orders));
    }

    @GetMapping("/orders/paid")
    public Result<List<Order>> getPaidOrders() {
        List<Order> orders = orderService.getPaidAndCompletedOrders();
        return Result.success(fillOrderDetails(orders));
    }

    @GetMapping("/orders/chef/{chefId}")
    public Result<Map<String, Object>> getOrdersByChefId(@PathVariable Long chefId) {
        Map<String, Object> result = new java.util.HashMap<>();
        Chef chef = chefService.getById(chefId);
        result.put("chef", chef);
        List<Order> orders = orderService.getOrdersByChefId(chefId);
        result.put("orders", fillOrderDetails(orders));
        return Result.success(result);
    }

    private List<Order> fillOrderDetails(List<Order> orders) {
        for (Order order : orders) {
            User user = userService.getById(order.getUserId());
            if (user != null) {
                order.setUsername(user.getUsername());
                order.setUserPhone(user.getPhone());
            }
            Chef chef = chefService.getById(order.getChefId());
            if (chef != null) {
                order.setChefName(chef.getName());
                order.setChefPhone(chef.getPhone());
            }
            List<OrderItem> items = orderItemService.getItemsByOrderId(order.getId());
            order.setDishes(items);
        }
        return orders;
    }

    @GetMapping("/chefs")
    public Result<List<Chef>> getAllChefs() {
        List<Chef> chefs = chefService.list();
        return Result.success(chefs);
    }

    @GetMapping("/users")
    public Result<List<User>> getAllUsers() {
        List<User> users = userService.list();
        return Result.success(users);
    }

    @GetMapping("/dishes")
    public Result<List<Dish>> getAllDishes() {
        List<Dish> dishes = dishService.list();
        return Result.success(dishes);
    }

    @GetMapping("/recharges")
    public Result<List<RechargeRecord>> getAllRechargeRecords() {
        List<RechargeRecord> records = rechargeRecordService.getAllRechargeRecords();
        return Result.success(records);
    }

    @PostMapping("/recharge/{id}/confirm")
    public Result<?> confirmRecharge(@PathVariable Long id) {
        try {
            boolean success = rechargeRecordService.confirmRecharge(id);
            return success ? Result.success() : Result.fail("确认失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/recharge/{id}/reject")
    public Result<?> rejectRecharge(@PathVariable Long id) {
        try {
            boolean success = rechargeRecordService.rejectRecharge(id);
            return success ? Result.success() : Result.fail("拒绝失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/pay-earnings")
    public Result<?> payEarnings(@RequestParam Long chefId, @RequestParam java.math.BigDecimal amount) {
        try {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("chefId", chefId);
            map.put("amount", amount);
            boolean success = chefService.payEarnings(map);
            return success ? Result.success() : Result.fail("发放失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/order/{orderId}/dispatch")
    public Result<?> dispatchChef(@PathVariable Long orderId) {
        try {
            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.fail("订单不存在");
            }

            if (order.getStatus() != 1) {
                return Result.fail("订单状态不正确，无法派遣");
            }

            order.setStatus(2);
            orderService.updateById(order);

            notificationService.createNotification(
                    "chef",
                    order.getChefId(),
                    orderId,
                    "新订单通知",
                    "您有新的订单，订单号：" + order.getOrderNo()
            );

            return Result.success();
        } catch (Exception e) {
            return Result.fail("派遣失败：" + e.getMessage());
        }
    }

    @PostMapping("/order/{orderId}/reject")
    public Result<?> rejectOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.fail("订单不存在");
            }

            User user = userService.getById(order.getUserId());
            if (user == null) {
                return Result.fail("用户不存在");
            }

            java.math.BigDecimal currentBalance = user.getBalance();
            if (currentBalance == null) {
                currentBalance = java.math.BigDecimal.ZERO;
            }
            user.setBalance(currentBalance.add(order.getPrice()));
            userService.updateById(user);

            order.setStatus(5);
            orderService.updateById(order);

            notificationService.clearOrderNotifications(orderId);

            return Result.success();
        } catch (Exception e) {
            return Result.fail("拒绝失败：" + e.getMessage());
        }
    }

    /**
     * 管理员审核取消订单（通过）
     */
    @PostMapping("/order/{orderId}/approve-cancel")
    public Result<?> approveCancelOrder(@PathVariable Long orderId) {
        try {
            boolean success = orderService.approveCancelOrder(orderId);
            return success ? Result.success() : Result.fail("审核失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 管理员审核取消订单（不通过）
     */
    @PostMapping("/order/{orderId}/reject-cancel")
    public Result<?> rejectCancelOrder(@PathVariable Long orderId) {
        try {
            boolean success = orderService.rejectCancelOrder(orderId);
            return success ? Result.success() : Result.fail("审核失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/dish/add")
    public Result<?> addDish(@RequestBody Dish dish) {
        boolean success = dishService.save(dish);
        return success ? Result.success() : Result.fail("添加失败");
    }

    @DeleteMapping("/dish/{id}")
    public Result<?> deleteDish(@PathVariable Long id) {
        boolean success = dishService.removeById(id);
        return success ? Result.success() : Result.fail("删除失败");
    }
}