// 文件位置: D:\cooking-service-platform\backend\src\main\java\com\cooking\service\impl\ChefSkillServiceImpl.java
package com.cooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.entity.ChefSkill;
import com.cooking.mapper.ChefSkillMapper;
import com.cooking.service.ChefSkillService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChefSkillServiceImpl extends ServiceImpl<ChefSkillMapper, ChefSkill> implements ChefSkillService {

    @Override
    public List<ChefSkill> getSkillsByChefId(Long chefId) {
        return this.list(new LambdaQueryWrapper<ChefSkill>()
                .eq(ChefSkill::getChefId, chefId));
    }
}
