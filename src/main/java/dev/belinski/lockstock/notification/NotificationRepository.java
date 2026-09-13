package dev.belinski.lockstock.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findTop5ByStatusOrderByCreatedAtAsc(NotificationStatus status);
}
