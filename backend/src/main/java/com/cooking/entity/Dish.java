package com.cooking.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 菜品实体类
 */
@Data
@TableName("dish")
public class Dish {
    /**
     * 菜品ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 菜品价格
     */
    private BigDecimal price;

    /**
     * 菜品描述
     */
    private String description;

    /**
     * 辣度等级（1-5）
     */
    private Integer spiciness;

    /**
     * 主要配料
     */
    private String ingredients;

    /**
     * 所属菜系
     */
    private String cuisine;

    /**
     * 菜品图片
     */
    private String image;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}