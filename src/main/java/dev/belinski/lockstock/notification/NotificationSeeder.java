package dev.belinski.lockstock.notification;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@ConditionalOnProperty(name = "app.seed-on-startup", havingValue = "true", matchIfMissing = true)
public class NotificationSeeder implements ApplicationRunner {

    private static final int COUNT = 20;

    private static final Logger log = LogManager.getLogger();

    private final NotificationRepository repository;

    public NotificationSeeder(NotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        Instant createdAt = Instant.now();

        for (int i = 1; i <= COUNT; i++) {
            String id = RandomStringUtils.insecure().nextAlphanumeric(5);
            Notification notification = new Notification(
                    "notif-"+id,
                    "Your order #%s is ready".formatted(id),
                    NotificationStatus.PENDING,
                    createdAt.plusMillis(i)
            );
            repository.save(notification);
        }

        log.info("Seeded {} pending notifications", COUNT);
    }

}
