package com.cooking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("review")
public class Review {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 用户ID（非数据库字段，通过orderId关联查询）
     */
    @TableField(exist = false)
    private Long userId;

    /**
     * 厨师ID（非数据库字段，通过orderId关联查询）
     */
    @TableField(exist = false)
    private Long chefId;

    /**
     * 评分
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}