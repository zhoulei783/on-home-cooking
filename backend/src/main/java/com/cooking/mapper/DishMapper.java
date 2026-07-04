// 文件位置: D:\cooking-service-platform\backend\src\main\java\com\cooking\mapper\DishMapper.java
package com.cooking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cooking.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
    // 继承BaseMapper，基本CRUD已具备

    // 可添加自定义方法：如根据菜系查询
    List<Dish> selectByCuisine(String cuisine);
}