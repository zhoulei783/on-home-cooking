package com.cooking.controller;

import com.cooking.common.Result;
import com.cooking.dto.NotificationDTO;
import com.cooking.service.NotificationService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/notification")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @GetMapping("/admin/unread")
    public Result<List<NotificationDTO>> getAdminUnreadNotifications() {
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications("admin", 0L);
        return Result.success(notifications);
    }

    @GetMapping("/chef/{chefId}/unread")
    public Result<List<NotificationDTO>> getChefUnreadNotifications(@PathVariable Long chefId) {
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications("chef", chefId);
        return Result.success(notifications);
    }

    @PutMapping("/{id}/read")
    public Result<?> markAsRead(@PathVariable Long id) {
        boolean success = notificationService.markAsRead(id);
        return success ? Result.success() : Result.fail("标记失败");
    }
}