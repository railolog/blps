package ru.ifmo.puls.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.ifmo.puls.common.NotificationMsgDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumerService {
    private final ObjectMapper objectMapper;
    private final NotificationQueryService notificationQueryService;

    @SneakyThrows
    @KafkaListener(id = "NotificationTopic", topics = "notification.topic", containerFactory = "singleFactory")
    public void consume(NotificationMsgDto dto) {
        log.info("=> consumed {}", objectMapper.writeValueAsString(dto));

        notificationQueryService.create(dto);
    }
}
