package ru.ifmo.puls.dto;

import java.util.List;

public record ListWithTotal<T>(
        List<T> items,
        long total
) {
}
