// 文件位置: D:\cooking-service-platform\backend\src\main\java\com\cooking\service\impl\DishServiceImpl.java
package com.cooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.entity.Dish;
import com.cooking.mapper.DishMapper;
import com.cooking.service.DishService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Override
    public List<Dish> getDishesByCuisine(String cuisine) {
        return this.list(new LambdaQueryWrapper<Dish>()
                .eq(Dish::getCuisine, cuisine));
    }
}