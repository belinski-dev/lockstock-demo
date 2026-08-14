package dev.belinski.lockstock.notification;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findTop5ByStatusOrderByCreatedAtAsc(NotificationStatus status);
}
