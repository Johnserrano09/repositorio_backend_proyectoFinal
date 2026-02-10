package com.portfolio.scheduler;

import com.portfolio.model.Advisory;
import com.portfolio.repository.AdvisoryRepository;
import com.portfolio.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdvisoryReminderScheduler {

    private final AdvisoryRepository advisoryRepository;
    private final NotificationService notificationService;

    /**
     * Runs every day at 10:00 AM to send reminders for tomorrow's advisories
     */
    @Scheduled(cron = "0 0 10 * * *")
    public void sendDailyReminders() {
        log.info("Running daily advisory reminder job");

        LocalDateTime startOfTomorrow = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfTomorrow = startOfTomorrow.plusDays(1);

        List<Advisory> tomorrowsAdvisories = advisoryRepository
                .findUpcomingApprovedAdvisories(startOfTomorrow, endOfTomorrow);

        log.info("Found {} advisories scheduled for tomorrow", tomorrowsAdvisories.size());

        for (Advisory advisory : tomorrowsAdvisories) {
            try {
                // Send reminder to programmer
                notificationService.sendAdvisoryReminderNotification(
                        advisory.getProgrammer(), advisory, "programmer");

                // Send reminder to external user
                notificationService.sendAdvisoryReminderNotification(
                        advisory.getExternal(), advisory, "external");

                log.info("Sent reminders for advisory: {}", advisory.getId());
            } catch (Exception e) {
                log.error("Failed to send reminder for advisory {}: {}", advisory.getId(), e.getMessage());
            }
        }

        log.info("Completed daily advisory reminder job");
    }
}
