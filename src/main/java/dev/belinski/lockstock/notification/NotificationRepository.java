package dev.belinski.lockstock.notification;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Notification> findTop5ByStatusOrderByCreatedAtAsc(NotificationStatus status);
}
