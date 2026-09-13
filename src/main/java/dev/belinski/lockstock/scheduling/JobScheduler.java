package dev.belinski.lockstock.scheduling;

import dev.belinski.lockstock.notification.NotificationService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
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
    @SchedulerLock(name = "notificationLock", lockAtLeastFor = "5s")
    public void notificationsScheduler() {
        notificationService.sendAndUpdateNotification(instanceId);
    }
}
