package com.cooking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单实体类
 */
@Data
@TableName("`order`") // order是MySQL关键字，需转义
public class Order {
    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(exist = false)
    private String chefPhone;
    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 厨师ID
     */
    private Long chefId;

    /**
     * 上门烹饪时间
     */
    private Date cookingTime;

    /**
     * 烹饪地址
     */
    private String address;

    /**
     * 订单金额
     */
    private BigDecimal price;

    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 用户姓名
     */
    @TableField(exist = false)
    private String username;

    /**
     * 用户电话
     */
    @TableField(exist = false)
    private String userPhone;

    /**
     * 厨师姓名
     */
    @TableField(exist = false)
    private String chefName;

    /**
     * 评分
     */
    @TableField(exist = false)
    private Integer rating;

    /**
     * 评价
     */
    @TableField(exist = false)
    private String comment;

    /**
     * 订单菜品列表（非数据库字段，用于前端展示）
     */
    @TableField(exist = false)
    private List<OrderItem> dishes;
}