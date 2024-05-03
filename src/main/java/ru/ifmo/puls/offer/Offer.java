package ru.ifmo.puls.offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Offer {
    private Long id;
    private String description;
    private Long price;
    private Long supplierId;
    private Long tenderId;
    private OfferStatus status;
}
