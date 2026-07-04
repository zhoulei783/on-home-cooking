// 文件位置: D:\cooking-service-platform\backend\src\main\java\com\cooking\service\ChefSkillService.java
package com.cooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.entity.ChefSkill;
import java.util.List;

public interface ChefSkillService extends IService<ChefSkill> {
    List<ChefSkill> getSkillsByChefId(Long chefId);
}