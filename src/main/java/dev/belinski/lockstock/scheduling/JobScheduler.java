package dev.belinski.lockstock.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class JobScheduler {

    Logger logger = Logger.getLogger(getClass().getName());

    @Scheduled(fixedRate = 1000)
    public void notificationsScheduler() {

        logger.info("Scheduler triggered");

    }

}
