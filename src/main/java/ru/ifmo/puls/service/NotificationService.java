package ru.ifmo.puls.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.ifmo.puls.common.NotificationMsgDto;
import ru.ifmo.puls.domain.Tender;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final KafkaTemplate<Long, NotificationMsgDto> kafkaTemplate;

    public void send(NotificationMsgDto dto) {
        kafkaTemplate.send("notification.topic", dto);
    }

    public void sendOfferNotification(Tender tender, String supplierName) {
        send(
                new NotificationMsgDto(
                        tender.getUserId(),
                        "Поступило новое предложение по заказу " + tender.getId(),
                        "Поставщик " + supplierName + " откликнулся на вашу заявку \"" + tender.getTitle() + "\""
                )
        );
    }

    public void declineOfferNotification(long supplierId, Tender tender) {
        send(
                new NotificationMsgDto(
                        supplierId,
                        "Ваше предложение по заказу \"" + tender.getTitle() + "\" было отклонено поставщиком",
                        "Номер заказа: " + tender.getId()
                )
        );
    }

    public void acceptOfferNotification(long supplierId, Tender tender) {
        send(
                new NotificationMsgDto(
                        supplierId,
                        "Ваше предложение по заказу \"" + tender.getTitle() + "\" было принято поставщиком",
                        "Номер заказа: " + tender.getId()
                )
        );
    }

    public void offerReminder(Tender tender) {
        send(
                new NotificationMsgDto(
                        tender.getUserId(),
                        "По вашей заявке \"" + tender.getTitle() + "\" есть непросмотренные предложения",
                        null
                )
        );
    }
}
