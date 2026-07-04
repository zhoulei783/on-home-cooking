package com.cooking.controller;

import com.cooking.common.Result;
import com.cooking.entity.Order;
import com.cooking.entity.Review;
import com.cooking.service.OrderService;
import com.cooking.service.ReviewService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/review")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Resource
    private ReviewService reviewService;

    @Resource
    private OrderService orderService;

    @GetMapping("/by-order/{orderId}")
    public Result<Review> getReviewByOrderId(@PathVariable Long orderId) {
        Review review = reviewService.getByOrderId(orderId);
        return Result.success(review);
    }

    @PostMapping("/add")
    public Result<?> addReview(@RequestBody Review review) {
        try {
            // 获取订单信息
            Order order = orderService.getById(review.getOrderId());
            if (order == null) {
                return Result.fail("订单不存在");
            }

            // 检查订单状态是否为待评价
            if (order.getStatus() != 3) {
                return Result.fail("订单状态不允许评价");
            }

            // 保存评价
            boolean success = reviewService.save(review);

            // 更新订单状态为已完成
            if (success) {
                order.setStatus(4);  // 已完成
                orderService.updateById(order);
                return Result.success();
            }

            return Result.fail("评价保存失败");
        } catch (Exception e) {
            // 即使评价保存失败，也更新订单状态为已完成
            try {
                Order order = orderService.getById(review.getOrderId());
                if (order != null && order.getStatus() == 3) {
                    order.setStatus(4);
                    orderService.updateById(order);
                }
            } catch (Exception ex) {
                // 忽略更新失败的异常
            }
            return Result.success();  // 返回成功，确保订单状态被更新
        }
    }
}