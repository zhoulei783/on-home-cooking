package com.cooking.service;

import com.cooking.dto.NotificationDTO;
import java.util.List;

public interface NotificationService {
    boolean createNotification(String receiverType, Long receiverId, Long orderId, String title, String content);
    List<NotificationDTO> getUnreadNotifications(String receiverType, Long receiverId);
    boolean markAsRead(Long notificationId);
    boolean clearOrderNotifications(Long orderId);
}