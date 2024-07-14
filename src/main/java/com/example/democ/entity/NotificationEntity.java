package com.example.democ.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class NotificationEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long chatId;
    private LocalDateTime notificationTime;
    private String message;
    private boolean isSent;
    private Long week;
    private int date;

    public NotificationEntity() {

    }


    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public NotificationEntity(Long chatId, LocalDateTime notificationTime, String message, Long week, int date){
        this.chatId = chatId;
        this.notificationTime = notificationTime;
        this.message = message;
        this.week = week;
        this.isSent = false;
        this.date = date;
    }


    public Long getWeek() {
        return week;
    }

    public void setWeek(Long week) {
        this.week = week;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public LocalDateTime getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(LocalDateTime notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }
}
