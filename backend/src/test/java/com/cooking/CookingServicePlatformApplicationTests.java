package com.cooking;

import com.cooking.entity.User;
import com.cooking.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;

@SpringBootTest
class CookingServicePlatformApplicationTests {

    @Resource
    private UserService userService;

    @Test
    void testUserRegister() {
        User user = new User();
        user.setUsername("测试用户");
        user.setPhone("13800138000");
        user.setPassword("123456");
        user.setAddress("北京市朝阳区");
        boolean success = userService.register(user);
        System.out.println("用户注册结果：" + success);
    }

}