package ru.ifmo.puls.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Tender {
    private Long id;
    private Long userId;
    private Long supplierId;
    private String title;
    private String description;
    private Long amount;
    private TenderStatus status;
}
