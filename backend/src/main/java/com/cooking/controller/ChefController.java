package com.cooking.controller;

import com.cooking.common.Result;
import com.cooking.entity.Chef;
import com.cooking.entity.ChefStatus;
import com.cooking.entity.Review;
import com.cooking.service.ChefService;
import com.cooking.service.ChefStatusService;
import com.cooking.service.ReviewService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 厨师控制器
 */
@RestController
@RequestMapping("/chef")
@CrossOrigin(origins = "*")
public class ChefController {

    @Resource
    private ChefService chefService;

    @Resource
    private ChefStatusService chefStatusService;

    @Resource
    private ReviewService reviewService;

    /**
     * 厨师登录
     */
    @PostMapping("/login")
    public Result<Chef> login(@RequestBody Chef chef) {
        String phone = chef.getPhone();
        String password = chef.getPassword();

        if (phone == null || phone.trim().isEmpty()) {
            return Result.fail("请输入手机号");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.fail("请输入密码");
        }

        Chef existingChef = chefService.getChefByPhoneAndPassword(phone, password);

        if (existingChef == null) {
            return Result.fail("手机号或密码错误");
        }

        existingChef.setPassword(null);
        return Result.success(existingChef);
    }

    /**
     * 添加厨师
     */
    @PostMapping("/add")
    public Result<?> addChef(@RequestBody Chef chef) {
        boolean success = chefService.save(chef);
        return success ? Result.success() : Result.fail("添加失败");
    }

    /**
     * 查询所有空闲厨师（默认查询今天，包含等级信息）
     */
    @GetMapping("/idle")
    public Result<List<Chef>> getIdleChefs() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return getIdleChefsByDate(today);
    }

    /**
     * 根据日期查询空闲厨师（包含等级信息）
     */
    @GetMapping("/idle/{date}")
    public Result<List<Chef>> getIdleChefsByDate(@PathVariable String date) {
        try {
            // 查询指定日期状态为空闲的厨师（status=0）
            List<ChefStatus> idleStatusList = chefStatusService.list(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ChefStatus>()
                            .eq("status_date", date)
                            .eq("status", 0)  // 0 表示空闲
            );

            // 提取空闲厨师的ID列表
            List<Long> idleChefIds = idleStatusList.stream()
                    .map(ChefStatus::getChefId)
                    .collect(Collectors.toList());

            // 如果没有空闲厨师，返回空列表
            if (idleChefIds.isEmpty()) {
                return Result.success(java.util.Collections.emptyList());
            }

            // 根据ID列表查询厨师信息（包含等级）
            List<Chef> chefs = ((com.cooking.service.impl.ChefServiceImpl)chefService).getChefsByIdsWithLevel(idleChefIds);

            return Result.success(chefs);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据日期查询空闲厨师（包含等级信息和评价信息）
     */
    @GetMapping("/idle-with-reviews/{date}")
    public Result<List<Map<String, Object>>> getIdleChefsWithReviewsByDate(@PathVariable String date) {
        try {
            // 查询指定日期状态为空闲的厨师（status=0）
            List<ChefStatus> idleStatusList = chefStatusService.list(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ChefStatus>()
                            .eq("status_date", date)
                            .eq("status", 0)  // 0 表示空闲
            );

            // 提取空闲厨师的ID列表
            List<Long> idleChefIds = idleStatusList.stream()
                    .map(ChefStatus::getChefId)
                    .collect(Collectors.toList());

            // 如果没有空闲厨师，返回空列表
            if (idleChefIds.isEmpty()) {
                return Result.success(java.util.Collections.emptyList());
            }

            // 根据ID列表查询厨师信息（包含等级、技能和评价）
            List<Map<String, Object>> chefs = ((com.cooking.service.impl.ChefServiceImpl)chefService).getChefsWithReviews(idleChefIds);

            return Result.success(chefs);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询空闲厨师（包含等级信息和评价信息，默认查询今天）
     */
    @GetMapping("/idle-with-reviews")
    public Result<List<Map<String, Object>>> getIdleChefsWithReviews() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return getIdleChefsWithReviewsByDate(today);
    }

    /**
     * 根据厨师ID查询评价列表
     */
    @GetMapping("/{chefId}/reviews")
    public Result<List<Review>> getChefReviews(@PathVariable Long chefId) {
        List<Review> reviews = reviewService.getByChefId(chefId);
        return Result.success(reviews != null ? reviews : java.util.Collections.emptyList());
    }

    /**
     * 根据厨师ID获取评价统计信息（平均评分和评价数量）
     */
    @GetMapping("/{chefId}/review-statistics")
    public Result<Map<String, Object>> getChefReviewStatistics(@PathVariable Long chefId) {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("chefId", chefId);
        statistics.put("averageRating", reviewService.getAverageRating(chefId));
        statistics.put("reviewCount", reviewService.getReviewCount(chefId));
        return Result.success(statistics);
    }

    /**
     * 根据菜系查询厨师
     */
    @GetMapping("/skill/{skill}")
    public Result<List<Chef>> getChefsBySkill(@PathVariable String skill) {
        List<Chef> chefs = chefService.getChefsBySkill(skill);
        return Result.success(chefs);
    }

    /**
     * 更新厨师状态（默认更新今天）
     */
    @PutMapping("/status/{chefId}/{status}")
    public Result<?> updateChefStatus(@PathVariable Long chefId, @PathVariable Integer status) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return updateChefStatusByDate(chefId, today, status);
    }

    /**
     * 更新指定日期的厨师状态
     */
    @PutMapping("/status/{chefId}/{date}/{status}")
    public Result<?> updateChefStatusByDate(@PathVariable Long chefId, @PathVariable String date, @PathVariable Integer status) {
        try {
            if (chefId == null || chefId <= 0) {
                return Result.fail("厨师ID无效");
            }
            if (date == null || date.trim().isEmpty()) {
                return Result.fail("日期不能为空");
            }
            if (status == null || (status != 0 && status != 1)) {
                return Result.fail("状态值无效，只能是0或1");
            }

            ChefStatus chefStatus = new ChefStatus();
            chefStatus.setChefId(chefId);
            chefStatus.setStatusDate(date);
            chefStatus.setStatus(status);
            Date now = new Date();
            chefStatus.setCreateTime(now);
            chefStatus.setUpdateTime(now);

            ChefStatus existing = chefStatusService.getOne(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ChefStatus>()
                            .eq("chef_id", chefId)
                            .eq("status_date", date)
            );

            boolean success;
            if (existing != null) {
                chefStatus.setId(existing.getId());
                chefStatus.setCreateTime(existing.getCreateTime());
                success = chefStatusService.updateById(chefStatus);
            } else {
                success = chefStatusService.save(chefStatus);
            }

            if (success) {
                return Result.success("状态更新成功");
            } else {
                return Result.fail("更新失败：数据库操作未成功");
            }
        } catch (Exception e) {
            System.err.println("更新厨师状态异常：");
            e.printStackTrace();
            return Result.fail("更新失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID查询厨师
     */
    @GetMapping("/{id}")
    public Result<Chef> getChefById(@PathVariable Long id) {
        Chef chef = chefService.getById(id);
        return Result.success(chef);
    }

    /**
     * 发放厨师收入
     */
    @PostMapping("/pay")
    public Result<?> payChefEarnings(@RequestBody Map<String, Object> params) {
        try {
            Long chefId = Long.parseLong(params.get("chefId").toString());
            Double amount = Double.parseDouble(params.get("amount").toString());

            Map<String, Object> payParams = new HashMap<>();
            payParams.put("chefId", chefId);
            payParams.put("amount", amount);

            boolean success = chefService.payEarnings(payParams);
            return success ? Result.success() : Result.fail("发放失败");
        } catch (Exception e) {
            return Result.fail("发放失败：" + e.getMessage());
        }
    }
}