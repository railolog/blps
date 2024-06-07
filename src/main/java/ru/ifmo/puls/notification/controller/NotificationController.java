package ru.ifmo.puls.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.blps.openapi.api.NotificationApi;
import ru.blps.openapi.model.NotificationAmountResponseTo;
import ru.blps.openapi.model.NotificationListResponseTo;
import ru.blps.openapi.model.NotificationResponseTo;
import ru.blps.openapi.model.NotificationShortResponseTo;
import ru.ifmo.puls.auth.service.UserService;
import ru.ifmo.puls.domain.User;
import ru.ifmo.puls.notification.model.Notification;
import ru.ifmo.puls.notification.model.NotificationAmount;
import ru.ifmo.puls.notification.service.NotificationQueryService;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {
    private final UserService userService;
    private final NotificationQueryService notificationQueryService;

    @Override
    public ResponseEntity<NotificationResponseTo> getMyNotification(Long id) {
        User user = userService.getCurrentUser();
        Notification notification = notificationQueryService.getById(user.getId(), id);

        return ResponseEntity.ok(
                new NotificationResponseTo()
                        .id(notification.getId())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .viewed(notification.getViewed())
        );
    }

    @Override
    public ResponseEntity<NotificationListResponseTo> getMyNotifications(Integer limit, Integer offset) {
        User user = userService.getCurrentUser();
        var notifications = notificationQueryService.getMyNotifications(user.getId(), limit, offset);

        return ResponseEntity.ok(new NotificationListResponseTo()
                .notifications(notifications.items().stream()
                        .map(t ->
                                new NotificationShortResponseTo()
                                        .id(t.getId())
                                        .title(t.getTitle())
                                        .viewed(t.getViewed())
                        )
                        .toList()
                )
                .total(notifications.total())
        );
    }

    @Override
    public ResponseEntity<NotificationAmountResponseTo> getMyNotificationsAmount() {
        User user = userService.getCurrentUser();
        NotificationAmount amount = notificationQueryService.getMyAmount(user.getId());

        return ResponseEntity.ok(
                new NotificationAmountResponseTo()
                        .total(amount.total())
                        .notViewed(amount.notViewed())
        );
    }

    @Override
    public ResponseEntity<Void> markAllViewed() {
        User user = userService.getCurrentUser();
        notificationQueryService.markAllViewed(user.getId());

        return ResponseEntity.ok().build();
    }
}
