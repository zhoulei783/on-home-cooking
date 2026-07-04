package com.cooking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;
import java.util.Date;

/**
 * 厨师状态表实体类
 * 以日期为单位记录厨师每天的空闲/忙碌状态
 */
@Data
@TableName("chef_status")
public class ChefStatus {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 厨师ID
     */
    private Long chefId;

    /**
     * 日期（如 2024-01-15）
     */
    private String statusDate;

    /**
     * 状态：0-空闲 1-忙碌（默认忙碌）
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}