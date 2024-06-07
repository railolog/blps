package ru.ifmo.puls.notification.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Notification {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private Boolean viewed;
}
