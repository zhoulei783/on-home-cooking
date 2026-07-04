package com.cooking.controller;

import com.cooking.common.Result;
import com.cooking.entity.Admin;
import com.cooking.entity.Order;
import com.cooking.entity.Payment;
import com.cooking.entity.RechargeRecord;
import com.cooking.entity.User;
import com.cooking.dto.BalanceDetailsDTO;
import com.cooking.dto.BalanceTransactionDTO;
import com.cooking.service.AdminService;
import com.cooking.service.OrderService;
import com.cooking.service.PaymentService;
import com.cooking.service.RechargeRecordService;
import com.cooking.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    
    @Resource
    private AdminService adminService;

    @Resource
    private RechargeRecordService rechargeRecordService;

    @Resource
    private OrderService orderService;

    @Resource
    private PaymentService paymentService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        try {
            boolean success = userService.register(user);
            return success ? Result.success() : Result.fail("注册失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String password = params.get("password");
        User user = userService.getUserByPhone(phone);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        if (!password.equals(user.getPassword())) {
            return Result.fail("密码错误");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 管理员登录（JSON body方式）
     */
    @PostMapping("/adminAuth/login")
    public Result<Admin> adminLogin(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String password = params.get("password");
        Admin admin = adminService.getAdminByPhone(phone);
        if (admin == null) {
            return Result.fail("管理员不存在");
        }
        if (!password.equals(admin.getPassword())) {
            return Result.fail("密码错误");
        }
        admin.setPassword(null);
        return Result.success(admin);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null); // 隐藏密码
        }
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<?> updateUser(@RequestBody User user) {
        user.setPassword(null);
        boolean success = userService.updateById(user);
        return success ? Result.success() : Result.fail("更新失败");
    }

    /**
     * 获取用户余额
     */
    @GetMapping("/{id}/balance")
    public Result<BigDecimal> getUserBalance(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        return Result.success(user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO);
    }

    /**
     * 获取用户余额总览和完整变动明细。
     */
    @GetMapping("/{id}/balance-details")
    public Result<BalanceDetailsDTO> getBalanceDetails(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        BalanceDetailsDTO details = new BalanceDetailsDTO();
        details.setBalance(user.getBalance() == null ? BigDecimal.ZERO : user.getBalance());
        List<BalanceTransactionDTO> transactions = new ArrayList<>();

        for (RechargeRecord record : rechargeRecordService.getRechargeRecordsByUserId(id)) {
            BalanceTransactionDTO item = new BalanceTransactionDTO();
            item.setId("recharge-" + record.getId());
            item.setType("recharge");
            item.setTitle("余额充值");
            item.setAmount(record.getAmount());
            item.setCreateTime(record.getCreateTime());

            if (record.getStatus() != null && record.getStatus() == 1) {
                item.setStatus("success");
                item.setDescription("充值已到账");
                item.setChangeAmount(record.getAmount());
                details.setTotalIncome(details.getTotalIncome().add(record.getAmount()));
            } else if (record.getStatus() != null && record.getStatus() == 2) {
                item.setStatus("rejected");
                item.setDescription("充值申请未通过，余额未发生变化");
                item.setChangeAmount(BigDecimal.ZERO);
            } else {
                item.setStatus("pending");
                item.setDescription("充值申请待管理员确认");
                item.setChangeAmount(BigDecimal.ZERO);
            }
            transactions.add(item);
        }

        List<Order> orders = orderService.getOrdersByUserId(id);
        Map<Long, Order> orderMap = new HashMap<>();
        for (Order order : orders) {
            orderMap.put(order.getId(), order);
        }

        if (!orderMap.isEmpty()) {
            LambdaQueryWrapper<Payment> paymentWrapper = new LambdaQueryWrapper<>();
            paymentWrapper.in(Payment::getOrderId, orderMap.keySet())
                    .eq(Payment::getStatus, 1);
            List<Payment> payments = paymentService.list(paymentWrapper);

            for (Payment payment : payments) {
                Order order = orderMap.get(payment.getOrderId());
                BalanceTransactionDTO expense = new BalanceTransactionDTO();
                expense.setId("payment-" + payment.getId());
                expense.setType("payment");
                expense.setTitle("订单支付");
                expense.setDescription("订单号 " + order.getOrderNo());
                expense.setAmount(payment.getAmount());
                expense.setChangeAmount(payment.getAmount().negate());
                expense.setStatus("success");
                expense.setCreateTime(payment.getPaymentTime());
                expense.setOrderNo(order.getOrderNo());
                expense.setTransactionId(payment.getTransactionId());
                transactions.add(expense);
                details.setTotalExpense(details.getTotalExpense().add(payment.getAmount()));

                // 已支付订单取消审核通过后会原路退回余额。
                if (order.getStatus() != null && order.getStatus() == 5) {
                    BalanceTransactionDTO refund = new BalanceTransactionDTO();
                    refund.setId("refund-" + payment.getId());
                    refund.setType("refund");
                    refund.setTitle("订单退款");
                    refund.setDescription("订单号 " + order.getOrderNo() + " · 取消审核通过");
                    refund.setAmount(payment.getAmount());
                    refund.setChangeAmount(payment.getAmount());
                    refund.setStatus("success");
                    // 旧数据未保存退款时间，使用支付时间保证流水仍可追溯。
                    refund.setCreateTime(payment.getPaymentTime());
                    refund.setOrderNo(order.getOrderNo());
                    refund.setTransactionId(payment.getTransactionId());
                    transactions.add(refund);
                    details.setTotalIncome(details.getTotalIncome().add(payment.getAmount()));
                }
            }
        }

        transactions.sort(
                Comparator.comparing(
                        BalanceTransactionDTO::getCreateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ).thenComparing(item -> "refund".equals(item.getType()) ? 0 : 1)
        );

        BigDecimal cursor = details.getBalance();
        for (BalanceTransactionDTO transaction : transactions) {
            if (transaction.getChangeAmount() != null
                    && transaction.getChangeAmount().compareTo(BigDecimal.ZERO) != 0) {
                transaction.setBalanceAfter(cursor);
                cursor = cursor.subtract(transaction.getChangeAmount());
            }
        }
        details.setTransactions(transactions);
        return Result.success(details);
    }

    /**
     * 确认充值（创建充值记录，等待管理员确认）
     */
    @PostMapping("/{id}/confirmRecharge")
    public Result<?> confirmRecharge(@PathVariable Long id, @RequestBody Map<String, String> params) {
        try {
            String amountStr = params.get("amount");
            // 验证金额不为空
            if (amountStr == null || amountStr.trim().isEmpty()) {
                return Result.fail("充值金额不能为空");
            }
            
            // 尝试转换金额
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr.trim());
            } catch (NumberFormatException e) {
                return Result.fail("金额格式错误，请输入有效的数字");
            }
            
            boolean success = userService.recharge(id, amount);
            return success ? Result.success() : Result.fail("充值失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }
}
