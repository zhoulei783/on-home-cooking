// 文件位置: D:\cooking-service-platform\backend\src\main\java\com\cooking\controller\ChefSkillController.java
package com.cooking.controller;

import com.cooking.common.Result;
import com.cooking.entity.ChefSkill;
import com.cooking.service.ChefSkillService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/chef-skill")
@CrossOrigin(origins = "*")
public class ChefSkillController {

    @Resource
    private ChefSkillService chefSkillService;

    @GetMapping("/chef/{chefId}")
    public Result<List<ChefSkill>> getSkillsByChefId(@PathVariable Long chefId) {
        return Result.success(chefSkillService.getSkillsByChefId(chefId));
    }

    @PostMapping("/add")
    public Result<?> addSkill(@RequestBody ChefSkill chefSkill) {
        return chefSkillService.save(chefSkill) ? Result.success() : Result.fail("添加失败");
    }
}