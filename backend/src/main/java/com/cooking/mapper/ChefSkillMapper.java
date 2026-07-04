// 文件位置: D:\cooking-service-platform\backend\src\main\java\com\cooking\mapper\ChefSkillMapper.java
package com.cooking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cooking.entity.ChefSkill;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ChefSkillMapper extends BaseMapper<ChefSkill> {
    List<ChefSkill> selectByChefId(Long chefId);
}