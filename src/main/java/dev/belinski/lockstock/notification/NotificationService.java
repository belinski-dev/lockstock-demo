package dev.belinski.lockstock.notification;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private static final Logger log = LogManager.getLogger();

    public void sendAndUpdateNotification(String instanceId) {
        List<Notification> pending = repository.findTop5ByStatusOrderByCreatedAtAsc(NotificationStatus.PENDING);

        if (pending.isEmpty()) { log.info("No pending notifications found"); }

        for (Notification notification : pending) {
            log.info("{} picked up {}", instanceId, notification.getId());

            try {
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(Instant.now());
                repository.save(notification);
                sendNotification(notification);
            } catch (InterruptedException _) {
                Thread.currentThread().interrupt();
            }

            log.info("Notification {} sent by INSTANCE {}", notification.getId(), instanceId);
        }
    }

    public void sendNotification(Notification notification) throws InterruptedException {
        log.info("Notification {} successfully sent", notification.getId());
        Thread.sleep(500);
    }
}
