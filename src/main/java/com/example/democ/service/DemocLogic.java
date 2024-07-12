package com.example.democ.service;

import com.example.democ.entity.NotificationEntity;
import com.example.democ.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DemocLogic {
    private final NotificationRepository notificationRepository;

    public DemocLogic(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotification(Long chatId, LocalDateTime notificationDate, String message) {
        NotificationEntity notification = new NotificationEntity(chatId, notificationDate, message);
        notificationRepository.save(notification);
    }
}
