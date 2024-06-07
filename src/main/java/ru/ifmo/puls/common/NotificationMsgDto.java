package ru.ifmo.puls.common;

public record NotificationMsgDto(
        long recipientId,
        String title,
        String message
) {
}
