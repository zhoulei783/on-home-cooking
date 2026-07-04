package com.cooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.entity.Review;

/**
 * 评价服务接口
 */
public interface ReviewService extends IService<Review> {

    /**
     * 根据订单ID查询评价
     */
    Review getByOrderId(Long orderId);

    /**
     * 根据厨师ID查询评价列表
     */
    java.util.List<Review> getByChefId(Long chefId);

    /**
     * 根据厨师ID获取平均评分
     */
    Double getAverageRating(Long chefId);

    /**
     * 根据厨师ID获取评价数量
     */
    Long getReviewCount(Long chefId);
}