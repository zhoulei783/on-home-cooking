// 文件位置: D:\cooking-service-platform\backend\src\main\java\com\cooking\service\impl\PaymentServiceImpl.java
package com.cooking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.entity.Payment;
import com.cooking.mapper.PaymentMapper;
import com.cooking.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {
}