package com.cooking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;
import java.util.Date;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密）
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 常用地址
     */
    private String address;

    /**
     * 余额
     */
    private java.math.BigDecimal balance;

    /**
     * 创建时间
     */
    private Date createTime;
}