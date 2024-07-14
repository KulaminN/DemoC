package com.example.democ.repository;

import com.example.democ.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    Collection<NotificationEntity> findByIsSentIsFalseAndNotificationTimeBefore(LocalDateTime dateTime);

}
