package dev.belinski.lockstock.notification;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("notifications")
@Data
public class Notification {

    @Id
    private String id;

    private String recipient;

    private String message;

    private NotificationStatus status;

    private Instant createdAt;

    private Instant sentAt;

    public Notification(String id, String recipient, String message, NotificationStatus status, Instant createdAt) {
        this.id = id;
        this.recipient = recipient;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

}
