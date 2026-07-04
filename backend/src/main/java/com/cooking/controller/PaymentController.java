// 文件位置: D:\cooking-service-platform\backend\src\main\java\com\cooking\controller\PaymentController.java
package com.cooking.controller;

import com.cooking.common.Result;
import com.cooking.entity.Payment;
import com.cooking.service.PaymentService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @GetMapping("/order/{orderId}")
    public Result<List<Payment>> getPaymentsByOrderId(@PathVariable Long orderId) {
        return Result.success(paymentService.list());
    }

    @PostMapping("/create")
    public Result<?> createPayment(@RequestBody Payment payment) {
        return paymentService.save(payment) ? Result.success() : Result.fail("创建失败");
    }
}