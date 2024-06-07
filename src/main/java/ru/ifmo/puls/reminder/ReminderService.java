package ru.ifmo.puls.reminder;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ifmo.puls.domain.Tender;
import ru.ifmo.puls.service.NotificationService;
import ru.ifmo.puls.service.OfferQueryService;
import ru.ifmo.puls.service.TenderQueryService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {
    private final OfferQueryService offerQueryService;
    private final TenderQueryService tenderQueryService;
    private final NotificationService notificationService;

    @Scheduled(cron = "${reminder.cron}")
//    @Scheduled(fixedDelayString = "PT01M")
    public void sendReminderToUsers() {
        List<Long> ignoredTenderIds = offerQueryService.getIgnoredTenderIds();
        List<Tender> tenders = tenderQueryService.findByIds(ignoredTenderIds);

        log.info("Sending reminders to [{}] tenders", tenders.size());

        tenders.forEach(notificationService::offerReminder);
    }
}
