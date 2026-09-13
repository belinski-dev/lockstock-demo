package dev.belinski.lockstock.notification;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;

    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private Instant createdAt;

    private Instant sentAt;

//    @Version
    private Long version;

    public Notification(String recipient, String message, NotificationStatus status, Instant createdAt) {
        this.recipient = recipient;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Notification() {

    }
}
