package dev.belinski.lockstock.notification;

import dev.belinski.lockstock.scheduling.JobScheduler;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = {
        "app.seed-on-startup=false",
        "app.instance-id=test",
        "app.notifications.cron=-"
})
@Testcontainers
class NotificationLockTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine").waitingFor(Wait.forListeningPort());

    @MockitoSpyBean
    NotificationRepository repository;

    @Autowired
    JobScheduler jobScheduler;

    @BeforeEach
    void clearNotifications() {
        repository.deleteAll();
    }

    @Test
    void schedulerSendsPendingNotifications() throws InterruptedException {
        List<Long> ids = fillDbWithNotifications();

        clearInvocations(repository); // forget the save() calls made while seeding

        int threads = ids.size();
        var barrier = new CyclicBarrier(threads);
        var pool = Executors.newFixedThreadPool(threads);

        ids.forEach(_ -> pool.submit(() -> {
            barrier.await(); //synchronization
            // send them all!
            jobScheduler.notificationsScheduler();
            return null;
        }));

        pool.shutdown();
        assertTrue(pool.awaitTermination(10, SECONDS));

        assertThat(repository.findAll()).allSatisfy(notification -> {
            assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SENT);
        });

        var sent = ArgumentCaptor.forClass(Notification.class);
        verify(repository, times(ids.size())).save(sent.capture());

        assertThat(sent.getAllValues())
                .extracting(Notification::getId)
                .containsExactlyInAnyOrderElementsOf(ids);
    }

    private @NonNull List<Long> fillDbWithNotifications() {
        Instant createdAt = Instant.now();
        List<Long> ids = List.of(1L, 2L, 3L, 4L, 5L);

        for (int i = 0; i < ids.size(); i++) {
            repository.save(new Notification(
                    "user-%d@example.com".formatted(i),
                    "message %d".formatted(i),
                    NotificationStatus.PENDING,
                    createdAt.plusMillis(i)
            ));
        }
        return ids;
    }
}
