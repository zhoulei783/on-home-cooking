package com.cooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.common.ChefLevelEnum;
import com.cooking.entity.Chef;
import com.cooking.entity.ChefLevel;
import com.cooking.entity.ChefSkill;
import com.cooking.entity.Review;
import com.cooking.mapper.ChefLevelMapper;
import com.cooking.mapper.ChefMapper;
import com.cooking.mapper.ChefSkillMapper;
import com.cooking.service.ChefService;
import com.cooking.service.ReviewService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class ChefServiceImpl extends ServiceImpl<ChefMapper, Chef> implements ChefService {

    @Resource
    private ChefMapper chefMapper;

    @Resource
    private ChefLevelMapper chefLevelMapper;

    @Resource
    private ChefSkillMapper chefSkillMapper;

    @Resource
    private ReviewService reviewService;

    @Override
    public Chef getChefByPhone(String phone) {
        return this.getOne(new QueryWrapper<Chef>().eq("phone", phone));
    }

    @Override
    public List<Chef> getChefsBySkill(String skill) {
        LambdaQueryWrapper<Chef> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Chef::getSkill, skill);
        return this.list(wrapper);
    }

    @Override
    public Chef getChefByPhoneAndPassword(String phone, String password) {
        return this.getOne(new QueryWrapper<Chef>().eq("phone", phone).eq("password", password));
    }

    @Override
    public boolean addEarnings(Map<String, Object> map) {
        return chefMapper.addEarnings(map) > 0;
    }

    @Override
    public boolean payEarnings(Map<String, Object> map) {
        return chefMapper.payEarnings(map) > 0;
    }

    @Override
    public boolean updateEarnings(Map<String, Object> map) {
        return chefMapper.updateEarnings(map) > 0;
    }

    /**
     * 根据ID列表查询厨师（包含等级信息和技能信息）
     */
    public List<Chef> getChefsByIdsWithLevel(List<Long> chefIds) {
        if (chefIds == null || chefIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询厨师信息
        List<Chef> chefs = this.list(new QueryWrapper<Chef>().in("id", chefIds));

        // 查询厨师职业资格等级信息
        System.out.println("=== 调试信息 ===");
        System.out.println("查询的厨师ID列表: " + chefIds);
        System.out.println("ChefLevelMapper 是否为空: " + (chefLevelMapper == null));
        
        List<ChefLevel> chefLevels = chefLevelMapper.selectList(
                new QueryWrapper<ChefLevel>().in("chef_id", chefIds)
        );
        System.out.println("查询到的厨师等级记录数: " + chefLevels.size());
        chefLevels.forEach(level -> 
            System.out.println("厨师ID: " + level.getChefId() + ", 等级编码: " + level.getLevelCode())
        );
        Map<Long, ChefLevel> levelMap = chefLevels.stream()
                .collect(Collectors.toMap(ChefLevel::getChefId, level -> level));

        // 查询厨师技能信息
        List<ChefSkill> chefSkills = chefSkillMapper.selectList(
                new QueryWrapper<ChefSkill>().in("chef_id", chefIds)
        );
        // 按厨师ID分组技能
        Map<Long, List<ChefSkill>> skillMap = chefSkills.stream()
                .collect(Collectors.groupingBy(ChefSkill::getChefId));

        // 为每个厨师添加等级和技能信息
        chefs.forEach(chef -> {
            // 获取厨师职业资格等级
            ChefLevel level = levelMap.get(chef.getId());
            String levelName = "未评级";
            if (level != null && level.getLevelCode() != null) {
                ChefLevelEnum levelEnum = ChefLevelEnum.getByCode(level.getLevelCode());
                levelName = levelEnum != null ? levelEnum.getDesc() : "未知";
            }
            System.out.println("厨师ID: " + chef.getId() + ", 姓名: " + chef.getName() + ", 等级: " + levelName);

            // 获取厨师技能列表并格式化
            List<ChefSkill> skills = skillMap.get(chef.getId());
            String skillStr = "";
            if (skills != null && !skills.isEmpty()) {
                skillStr = skills.stream()
                        .map(skill -> skill.getSkillName() + "——水平" + skill.getLevel())
                        .collect(Collectors.joining(";"));
            }

            // 组合格式：等级；擅长：技能1——水平X;技能2——水平Y
            chef.setSkill(levelName + "；擅长：" + skillStr);
            System.out.println("最终skill字段: " + chef.getSkill());
        });

        return chefs;
    }

    /**
     * 根据ID列表查询厨师（包含等级信息、技能信息和评价信息）
     */
    public List<Map<String, Object>> getChefsWithReviews(List<Long> chefIds) {
        if (chefIds == null || chefIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询厨师信息
        List<Chef> chefs = this.list(new QueryWrapper<Chef>().in("id", chefIds));

        // 查询厨师职业资格等级信息
        List<ChefLevel> chefLevels = chefLevelMapper.selectList(
                new QueryWrapper<ChefLevel>().in("chef_id", chefIds)
        );
        Map<Long, ChefLevel> levelMap = chefLevels.stream()
                .collect(Collectors.toMap(ChefLevel::getChefId, level -> level));

        // 查询厨师技能信息
        List<ChefSkill> chefSkills = chefSkillMapper.selectList(
                new QueryWrapper<ChefSkill>().in("chef_id", chefIds)
        );
        Map<Long, List<ChefSkill>> skillMap = chefSkills.stream()
                .collect(Collectors.groupingBy(ChefSkill::getChefId));

        // 转换为Map列表，包含评价信息
        List<Map<String, Object>> result = chefs.stream().map(chef -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", chef.getId());
            map.put("name", chef.getName());
            map.put("phone", chef.getPhone());
            map.put("price", chef.getPrice());
            
            // 获取厨师职业资格等级
            ChefLevel level = levelMap.get(chef.getId());
            String levelName = "未评级";
            if (level != null && level.getLevelCode() != null) {
                ChefLevelEnum levelEnum = ChefLevelEnum.getByCode(level.getLevelCode());
                levelName = levelEnum != null ? levelEnum.getDesc() : "未知";
            }
            map.put("level", levelName);

            // 获取厨师技能列表并格式化
            List<ChefSkill> skills = skillMap.get(chef.getId());
            String skillStr = "";
            if (skills != null && !skills.isEmpty()) {
                skillStr = skills.stream()
                        .map(skill -> skill.getSkillName() + "——水平" + skill.getLevel())
                        .collect(Collectors.joining(";"));
            }
            map.put("skill", skillStr);

            // 获取评价信息
            Double avgRating = reviewService.getAverageRating(chef.getId());
            Long reviewCount = reviewService.getReviewCount(chef.getId());
            List<Review> reviews = reviewService.getByChefId(chef.getId());
            
            map.put("averageRating", avgRating);
            map.put("reviewCount", reviewCount);
            map.put("reviews", reviews != null ? reviews : Collections.emptyList());
            
            return map;
        }).collect(Collectors.toList());

        return result;
    }
}