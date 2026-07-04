package com.cooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.entity.Order;
import com.cooking.entity.Review;
import com.cooking.mapper.OrderMapper;
import com.cooking.mapper.ReviewMapper;
import com.cooking.service.ReviewService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 评价服务实现类
 */
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Resource
    private OrderMapper orderMapper;

    @Override
    public Review getByOrderId(Long orderId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getOrderId, orderId);
        Review review = this.getOne(wrapper);
        
        // 通过订单ID关联查询用户ID和厨师ID
        if (review != null && review.getOrderId() != null) {
            Order order = orderMapper.selectById(review.getOrderId());
            if (order != null) {
                review.setUserId(order.getUserId());
                review.setChefId(order.getChefId());
            }
        }
        
        return review;
    }

    @Override
    public List<Review> getByChefId(Long chefId) {
        // 先查询所有评价
        List<Review> allReviews = this.list();
        List<Review> chefReviews = new ArrayList<>();
        
        // 通过订单ID关联筛选属于指定厨师的评价
        for (Review review : allReviews) {
            if (review.getOrderId() != null) {
                Order order = orderMapper.selectById(review.getOrderId());
                if (order != null && chefId.equals(order.getChefId())) {
                    review.setUserId(order.getUserId());
                    review.setChefId(order.getChefId());
                    chefReviews.add(review);
                }
            }
        }
        
        // 按创建时间降序排序
        chefReviews.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
        
        return chefReviews;
    }

    @Override
    public Double getAverageRating(Long chefId) {
        List<Review> reviews = getByChefId(chefId);
        if (reviews == null || reviews.isEmpty()) {
            return null;
        }
        double sum = reviews.stream()
                .mapToInt(Review::getRating)
                .sum();
        return Math.round(sum / reviews.size() * 10) / 10.0;
    }

    @Override
    public Long getReviewCount(Long chefId) {
        return (long) getByChefId(chefId).size();
    }
}