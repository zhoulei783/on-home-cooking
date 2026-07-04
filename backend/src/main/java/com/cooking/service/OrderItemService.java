// 文件位置: D:\cooking-service-platform\backend\src\main\java\com\cooking\service\OrderItemService.java
package com.cooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.entity.OrderItem;
import java.util.List;

public interface OrderItemService extends IService<OrderItem> {
    List<OrderItem> getItemsByOrderId(Long orderId);
}