// 文件位置: D:\cooking-service-platform\backend\src\main\java\com\cooking\service\DishService.java
package com.cooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.entity.Dish;
import java.util.List;

public interface DishService extends IService<Dish> {
    List<Dish> getDishesByCuisine(String cuisine);
}