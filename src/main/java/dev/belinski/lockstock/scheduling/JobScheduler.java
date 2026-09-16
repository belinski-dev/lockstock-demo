package dev.belinski.lockstock.scheduling;

import dev.belinski.lockstock.notification.NotificationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobScheduler {

    private static final Logger log = LogManager.getLogger();

    private final NotificationService notificationService;

    private final String instanceId;

    public JobScheduler(NotificationService notificationService, @Value("${app.instance-id}") String instanceId) {
        this.notificationService = notificationService;
        this.instanceId = instanceId;
    }

    @Scheduled(cron = "*/2 * * * * *")
    public void notificationsScheduler() {
        try {
            notificationService.sendAndUpdateNotification(instanceId);
        } catch (
                CannotAcquireLockException e) {
            log.info("{} could not acquire lock — skipping", instanceId);
        }
    }
}
