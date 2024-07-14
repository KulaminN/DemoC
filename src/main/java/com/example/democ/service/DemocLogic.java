package com.example.democ.service;

import com.example.democ.controller.DemocControl;
import com.example.democ.entity.NotificationEntity;
import com.example.democ.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class DemocLogic {
    public static boolean flag = false;
    private final NotificationRepository notificationRepository;
    private final TelegramClient telegramClient;

    public DemocLogic(NotificationRepository notificationRepository, TelegramClient telegramClient) {
        this.notificationRepository = notificationRepository;
        this.telegramClient = telegramClient;
    }

    public void createNotification(Long chatId, LocalDateTime notificationDate, String message, Long week, int date) {
        NotificationEntity notification = new NotificationEntity(chatId, notificationDate, message, week,date);
        notificationRepository.save(notification);
    }

    @Scheduled(fixedRate = 3000)
    public void sendScheduledNotifications() throws TelegramApiException {
        Collection<NotificationEntity> notifications = notificationRepository.findByIsSentIsFalseAndNotificationTimeBefore(LocalDateTime.now());
        for (NotificationEntity notification : notifications) {
            SendMessage message = SendMessage
                    .builder()
                    .chatId(notification.getChatId())
                    .text(notification.getMessage())
                    .build();
            telegramClient.execute(message);
            flag = true;
            notification.setSent(true);
            System.out.println("HI");
            notificationRepository.save(notification);
        }
    }
}
