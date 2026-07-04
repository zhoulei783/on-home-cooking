package com.cooking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 厨师技能实体类
 */
@Data
@TableName("chef_skill")
public class ChefSkill {
    /**
     * 技能ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 厨师ID
     */
    private Long chefId;

    /**
     * 技能名称（如川菜、粤菜、淮扬菜等）
     */
    private String skillName;

    /**
     * 技能等级（1-5）
     */
    private Integer level;

    /**
     * 创建时间
     */
    private Date createTime;
}