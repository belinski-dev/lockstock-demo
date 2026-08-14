package dev.belinski.lockstock.notification;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(properties = {
        "app.seed-on-startup=false",
        "app.instance-id=test"
})
@Testcontainers
class NotificationSchedulerTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongodb = new MongoDBContainer("mongo:8");

    @Autowired
    NotificationRepository repository;

    @BeforeEach
    void clearNotifications() {
        repository.deleteAll();
    }

    @Test
    void schedulerSendsPendingNotifications() {
        List<String> ids = fillDbWithNotifications();

        await().atMost(Duration.ofSeconds(60))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> assertThat(repository.findAllById(ids))
                        .hasSize(ids.size())
                        .allSatisfy(notification -> {
                            assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SENT);
                            assertThat(notification.getSentAt()).isNotNull();
                        }));
    }

    private @NonNull List<String> fillDbWithNotifications() {
        Instant createdAt = Instant.now();
        List<String> ids = List.of( "test-00", "test-01", "test-02", "test-03", "test-04");

        for (int i = 0; i < ids.size(); i++) {
            repository.save(new Notification(
                    ids.get(i),
                    "user-%d@example.com".formatted(i),
                    "message %d".formatted(i),
                    NotificationStatus.PENDING,
                    createdAt.plusMillis(i)
            ));
        }
        return ids;
    }
}
