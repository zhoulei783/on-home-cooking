package com.cooking.service.impl;

import com.cooking.dto.NotificationDTO;
import com.cooking.service.NotificationService;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final Map<Long, NotificationDTO> notificationStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public boolean createNotification(String receiverType, Long receiverId, Long orderId, String title, String content) {
        NotificationDTO notification = new NotificationDTO();
        notification.setId(idGenerator.getAndIncrement());
        notification.setReceiverType(receiverType);
        notification.setReceiverId(receiverId);
        notification.setOrderId(orderId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setStatus(0);
        notification.setCreateTime(new Date());
        notificationStore.put(notification.getId(), notification);
        return true;
    }

    @Override
    public List<NotificationDTO> getUnreadNotifications(String receiverType, Long receiverId) {
        return notificationStore.values().stream()
                .filter(n -> n.getReceiverType().equals(receiverType)
                        && n.getReceiverId().equals(receiverId)
                        && n.getStatus() == 0)
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean markAsRead(Long notificationId) {
        NotificationDTO notification = notificationStore.get(notificationId);
        if (notification != null) {
            notification.setStatus(1);
            return true;
        }
        return false;
    }

    @Override
    public boolean clearOrderNotifications(Long orderId) {
        notificationStore.values().removeIf(n -> orderId.equals(n.getOrderId()));
        return true;
    }
}