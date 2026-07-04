package com.cooking.controller;

import com.cooking.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页控制器（健康检查）
 */
@RestController
@RequestMapping("")
public class HomeController {

    /**
     * 健康检查接口
     */
    @GetMapping("")
    public Result<?> healthCheck() {
        return Result.success("上门烹饪服务平台运行正常");
    }

    /**
     * 健康检查接口（带路径）
     */
    @GetMapping("/health")
    public Result<?> healthCheckWithPath() {
        return Result.success("上门烹饪服务平台运行正常");
    }
}