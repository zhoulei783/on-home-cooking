package com.cooking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 厨师实体类
 */
@Data
@TableName("chef")
public class Chef {
    /**
     * 厨师ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 厨师姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 擅长菜系
     */
    private String skill;

    /**
     * 基础服务费
     */
    private BigDecimal price;

    /**
     * 密码
     */
    private String password;

    /**
     * 总收入
     */
    private BigDecimal totalEarnings;

    /**
     * 已发放
     */
    private BigDecimal paidAmount;

    /**
     * 剩余
     */
    private BigDecimal remainingAmount;

    /**
     * 创建时间
     */
    private Date createTime;
}