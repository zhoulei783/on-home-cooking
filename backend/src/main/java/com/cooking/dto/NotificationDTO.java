package com.cooking.dto;

import lombok.Data;
import java.util.Date;

@Data
public class NotificationDTO {
    private Long id;
    private String receiverType;
    private Long receiverId;
    private Long orderId;
    private String title;
    private String content;
    private Integer status;
    private Date createTime;
}
