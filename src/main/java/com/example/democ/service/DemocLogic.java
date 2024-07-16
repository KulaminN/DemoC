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
import java.time.LocalTime;
import java.util.Collection;

@Service
public class DemocLogic {
    public static boolean flag = false;
    private final NotificationRepository notificationRepository;
    private final TelegramClient telegramClient;
    int counter = 0;
    int[][] pod = {{6, 5, 5, 4, 3},
            {7, 6, 5, 4, 4},
            {8, 6, 5, 5, 4},
            {8, 7, 5, 5, 5},
            {9, 7, 6, 5, 5},
            {10, 7, 6, 6, 5},
            {10, 8, 6, 6, 6},
            {11, 8, 7, 6, 6},
            {12, 8, 7, 6, 6},
            {12, 9, 7, 7, 7},
            {13, 9, 8, 7, 7},
            {14, 9, 8, 8, 7}
    };
    int[][] otj = {{20, 20, 15, 15, 10},
            {25, 25, 20, 15, 10},
            {30, 30, 25, 20, 10},
            {35, 30, 25, 20, 15},
            {40, 35, 25, 25, 15},
            {40, 40, 30, 30, 20},
            {45, 40, 35, 35, 25},
            {45, 45, 35, 35, 25},
            {50, 45, 35, 35, 30},
            {50, 50, 40, 40, 35},
            {55, 50, 40, 40, 35},
            {60, 55, 40, 40, 35},
            {60, 60, 45, 45, 40},
            {65, 60, 45, 45, 40},
            {65, 65, 45, 45, 40}
    };
    int[][] br = {{10, 5, 5, 3, 2},
            {15, 15, 10, 5, 5},
            {20, 20, 15, 15, 10},
            {25, 25, 20, 15, 10},
            {30, 30, 25, 20, 15},
            {35, 30, 25, 20, 15},
            {40, 35, 25, 25, 15},
            {40, 40, 30, 30, 20},
            {45, 40, 35, 35, 25},
            {45, 45, 35, 35, 25},
            {50, 45, 35, 35, 30},
            {50, 50, 40, 40, 35},
            {55, 50, 40, 40, 35},
            {60, 55, 40, 40, 35},
            {60, 60, 45, 45, 40},
            {65, 60, 45, 45, 40},
            {65, 65, 45, 45, 40}
    };

    public DemocLogic(NotificationRepository notificationRepository, TelegramClient telegramClient) {
        this.notificationRepository = notificationRepository;
        this.telegramClient = telegramClient;
    }

    public void createNotification(Long chatId, LocalDateTime notificationDate, String message, Long week, int date) {
        NotificationEntity notification = new NotificationEntity(chatId, notificationDate, message, week, date);
        notificationRepository.save(notification);
    }

    @Scheduled(fixedRate = 3000)
    public void sendScheduledNotifications() throws TelegramApiException {
        Collection<NotificationEntity> notifications = notificationRepository.findByIsSentIsFalseAndNotificationTimeBefore(LocalDateTime.now());
        for (NotificationEntity notification : notifications) {
            Long week = notification.getWeek();
            String mess = "";
            if(counter>=7){
                week++;
                counter = 0;
            }
            switch (notification.getMessage()){
                case "Отжимания"->{
                    mess = "Сегодня вам нужно сделать отжимания:" +
                            "\n1 подход: "+ otj[Math.toIntExact(week-1)][0] +
                            "\n2 подход: "+ otj[Math.toIntExact(week-1)][1] +
                            "\n3 подход: "+ otj[Math.toIntExact(week-1)][2] +
                            "\n4 подход: "+ otj[Math.toIntExact(week-1)][3] +
                            "\n5 подход: "+ otj[Math.toIntExact(week-1)][4];
                }
                case "Подтягивания"->{
                    mess = "Сегодня вам нужно сделать подтягивания:" +
                            "\n1 подход: "+ pod[Math.toIntExact(week-1)][0] +
                            "\n2 подход: "+ pod[Math.toIntExact(week-1)][1] +
                            "\n3 подход: "+ pod[Math.toIntExact(week-1)][2] +
                            "\n4 подход: "+ pod[Math.toIntExact(week-1)][3] +
                            "\n5 подход: "+ pod[Math.toIntExact(week-1)][4];
                }
                case "Брусья"->{
                    mess = "Сегодня вам нужно сделать отжимания на брусьях:" +
                            "\n1 подход: "+ br[Math.toIntExact(week-1)][0] +
                            "\n2 подход: "+ br[Math.toIntExact(week-1)][1] +
                            "\n3 подход: "+ br[Math.toIntExact(week-1)][2] +
                            "\n4 подход: "+ br[Math.toIntExact(week-1)][3] +
                            "\n5 подход: "+ br[Math.toIntExact(week-1)][4];
                }
            }
            counter+=2;
            SendMessage message = SendMessage
                    .builder()
                    .chatId(notification.getChatId())
                    .text(mess)
                    .build();
            telegramClient.execute(message);
            flag = true;
            notification.setSent(true);
            System.out.println("HI");
            notificationRepository.save(notification);
        }
    }
}
