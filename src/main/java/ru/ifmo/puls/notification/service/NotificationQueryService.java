package ru.ifmo.puls.notification.service;

import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.ifmo.puls.LimitOffsetPageRequest;
import ru.ifmo.puls.common.NotificationMsgDto;
import ru.ifmo.puls.dto.ListWithTotal;
import ru.ifmo.puls.exception.ForbiddenException;
import ru.ifmo.puls.exception.NotFoundException;
import ru.ifmo.puls.notification.model.Notification;
import ru.ifmo.puls.notification.model.NotificationAmount;
import ru.ifmo.puls.notification.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {
    private final NotificationRepository repository;

    public Notification create(NotificationMsgDto notification) {
        return repository.create(notification);
    }

    public NotificationAmount getMyAmount(long userId) {
        return repository.getAmountByUserId(userId);
    }

    public ListWithTotal<Notification> getMyNotifications(long userId, int limit, int offset) {
        Page<Notification> notifications = repository
                .findByUserId(LimitOffsetPageRequest.of(limit, offset), userId);
        return new ListWithTotal<>(notifications.stream().toList(), notifications.getTotalElements());
    }

    public Notification getById(long userId, long id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("There are no notification with id " + id));

        if (!Objects.equals(notification.getUserId(), userId)) {
            throw ForbiddenException.fromUserId(userId);
        }

        notification.setViewed(true);
        return repository.update(notification);
    }

    public void markAllViewed(long userId) {
        repository.markViewedByUserId(userId);
    }
}
