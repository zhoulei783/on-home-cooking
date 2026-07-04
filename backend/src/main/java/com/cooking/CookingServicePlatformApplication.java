package com.cooking;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 上门烹饪服务平台启动类
 */
@SpringBootApplication
@MapperScan("com.cooking.mapper") // 扫描MyBatis-Plus Mapper接口
public class CookingServicePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(CookingServicePlatformApplication.class, args);
        System.out.println("======= 上门烹饪服务平台启动成功 =======");
    }

}