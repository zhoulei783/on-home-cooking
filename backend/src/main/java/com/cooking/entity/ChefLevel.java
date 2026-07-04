package com.cooking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;
import java.util.Date;

/**
 * 厨师等级实体类
 * 记录厨师的职业资格等级信息
 */
@Data
@TableName("chef_level")
public class ChefLevel {
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
     * 等级编码：1-初级 2-中级 3-高级 4-技师 5-高级技师
     */
    private Integer levelCode;
}