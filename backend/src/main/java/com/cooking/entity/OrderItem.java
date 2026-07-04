package com.cooking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单项实体类
 */
@Data
@TableName("order_item")
public class OrderItem {
    /**
     * 订单项ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 菜品ID
     */
    private Long dishId;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 菜品名称（非数据库字段，通过dishId关联查询）
     */
    @TableField(exist = false)
    private String dishName;

    /**
     * 单价（非数据库字段，通过dishId关联查询）
     */
    @TableField(exist = false)
    private BigDecimal price;

    /**
     * 小计金额
     */
    private BigDecimal subtotal;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}