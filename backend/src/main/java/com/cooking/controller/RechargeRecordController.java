package com.cooking.controller;

import com.cooking.common.Result;
import com.cooking.entity.RechargeRecord;
import com.cooking.service.RechargeRecordService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 充值记录控制器
 */
@RestController
@RequestMapping("/recharge")
@CrossOrigin(origins = "*")
public class RechargeRecordController {

    @Resource
    private RechargeRecordService rechargeRecordService;

    /**
     * 用户充值
     */
    @PostMapping("/add")
    public Result<?> recharge(@RequestParam Long userId, @RequestParam BigDecimal amount) {
        try {
            boolean success = rechargeRecordService.createRechargeRecord(userId, "", "", amount);
            return success ? Result.success() : Result.fail("充值失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取用户充值记录
     */
    @GetMapping("/records/{userId}")
    public Result<List<RechargeRecord>> getRechargeRecords(@PathVariable Long userId) {
        return Result.success(rechargeRecordService.getRechargeRecordsByUserId(userId));
    }
}
