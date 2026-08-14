package dev.belinski.lockstock.notification;

import dev.belinski.lockstock.scheduling.JobScheduler;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
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
    static MongoDBContainer mongodb = new MongoDBContainer("mongo:8");

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
        List<String> ids = fillDbWithNotifications();

        clearInvocations(repository); // forget the save() calls made while seeding

//        int threads = ids.size();
//        var barrier = new CyclicBarrier(threads);
//        var pool = Executors.newFixedThreadPool(threads);
//
//        ids.forEach(_ -> pool.submit(() -> {
//            barrier.await(); // waiting at the barrier to ensure simultaneous execution
//            jobScheduler.notificationsScheduler();
//            return null;
//        }));
//
//        pool.shutdown();
//        assertTrue(pool.awaitTermination(10, SECONDS));

        // send them all!
        jobScheduler.notificationsScheduler();

        assertThat(repository.findAll()).allSatisfy(notification -> {
            assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SENT);
        });

//        var sent = ArgumentCaptor.forClass(Notification.class);
//        verify(repository, times(ids.size())).save(sent.capture());

//        assertThat(sent.getAllValues())
//                .extracting(Notification::getId)
//                .containsExactlyInAnyOrderElementsOf(ids);
    }

    private @NonNull List<String> fillDbWithNotifications() {
        Instant createdAt = Instant.now();
        List<String> ids = List.of("test-00", "test-01", "test-02", "test-03", "test-04");

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
