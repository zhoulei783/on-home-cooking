// D:\cooking-service-platform\backend\src\main\java\com\cooking\entity\RechargeRecord.java
package com.cooking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("recharge_record")
public class RechargeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户姓名（非数据库字段，通过userId关联查询）
     */
    @TableField(exist = false)
    private String userName;
    
    /**
     * 用户电话（非数据库字段，通过userId关联查询）
     */
    @TableField(exist = false)
    private String userPhone;
    
    /**
     * 充值金额
     */
    private BigDecimal amount;
    
    /**
     * 状态（0-待确认，1-已确认，2-已拒绝）
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
}