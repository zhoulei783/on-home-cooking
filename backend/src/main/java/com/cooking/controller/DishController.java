// 文件位置: D:\cooking-service-platform\backend\src\main\java\com\cooking\controller\DishController.java
package com.cooking.controller;

import com.cooking.common.Result;
import com.cooking.entity.Dish;
import com.cooking.service.DishService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/dish")
@CrossOrigin(origins = "*")
public class DishController {

    @Resource
    private DishService dishService;

    @GetMapping("/list")
    public Result<List<Dish>> getAllDishes() {
        return Result.success(dishService.list());
    }

    @GetMapping("/all")
    public Result<List<Dish>> getAllDishesForAdmin() {
        return Result.success(dishService.list());
    }

    @GetMapping("/cuisine/{cuisine}")
    public Result<List<Dish>> getDishesByCuisine(@PathVariable String cuisine) {
        return Result.success(dishService.getDishesByCuisine(cuisine));
    }

    @PostMapping("/add")
    public Result<?> addDish(@RequestBody Dish dish) {
        return dishService.save(dish) ? Result.success() : Result.fail("添加失败");
    }

    @PutMapping("/{id}")
    public Result<?> updateDish(@PathVariable Long id, @RequestBody Dish dish) {
        dish.setId(id);
        boolean updateSuccess = dishService.updateById(dish);
        if (updateSuccess) {
            return Result.success();
        }
        return Result.fail("修改失败");
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteDish(@PathVariable Long id) {
        return dishService.removeById(id) ? Result.success() : Result.fail("删除失败");
    }
}