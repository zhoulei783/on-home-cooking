// 文件位置: D:\cooking-service-platform\backend\src\main\java\com\cooking\service\impl\OrderItemServiceImpl.java
package com.cooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.entity.Dish;
import com.cooking.entity.OrderItem;
import com.cooking.mapper.OrderItemMapper;
import com.cooking.service.DishService;
import com.cooking.service.OrderItemService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Resource
    private DishService dishService;

    @Override
    public List<OrderItem> getItemsByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = this.list(wrapper);
        
        // 通过菜品ID关联查询菜品信息
        for (OrderItem item : items) {
            if (item.getDishId() != null) {
                Dish dish = dishService.getById(item.getDishId());
                if (dish != null) {
                    item.setDishName(dish.getName());
                    item.setPrice(dish.getPrice());
                }
            }
        }
        
        return items;
    }
}