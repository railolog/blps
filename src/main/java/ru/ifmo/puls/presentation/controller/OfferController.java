package ru.ifmo.puls.presentation.controller;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;
import ru.blps.openapi.api.OfferApi;
import ru.blps.openapi.model.CreateOfferRequestTo;
import ru.blps.openapi.model.CreateOfferResponseTo;
import ru.blps.openapi.model.OfferListResponseTo;
import ru.blps.openapi.model.OfferResponseTo;
import ru.ifmo.puls.auth.service.UserService;
import ru.ifmo.puls.domain.Offer;
import ru.ifmo.puls.service.OfferManagementService;
import ru.ifmo.puls.service.OfferQueryService;

@RestController
@RequiredArgsConstructor
public class OfferController implements OfferApi {
    private final OfferManagementService offerManagementService;
    private final OfferQueryService offerQueryService;
    private final UserService userService;

    @Override
    @Secured("SUPPLIER")
    public ResponseEntity<CreateOfferResponseTo> createOffer(CreateOfferRequestTo createOfferRequestTo) {
        long id = offerManagementService.createOffer(createOfferRequestTo, userService.getCurrentUser());
        return ResponseEntity.ok(
                new CreateOfferResponseTo().id(id)
        );
    }

    @Override
    public ResponseEntity<OfferResponseTo> getOfferById(Long id) {
        Optional<Offer> offerOpt = offerQueryService.findById(id);

        return offerOpt
                .map(offer -> ResponseEntity.ok(transform(offer)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<OfferListResponseTo> getOffersByTenderId(Long id) {
        return ResponseEntity.ok(
                transform(offerManagementService.findByTenderId(id, userService.getCurrentUser()))
        );
    }

    @Override
    @Secured("SUPPLIER")
    public ResponseEntity<OfferListResponseTo> getSupplierOffers() {
        return ResponseEntity.ok(
                transform(
                        offerQueryService.findBySupplierId(userService.getCurrentUser().getId())
                )
        );
    }

    private OfferListResponseTo transform(List<Offer> offers) {
        return new OfferListResponseTo()
                .offers(
                        offers.stream()
                                .map(this::transform)
                                .toList()
                );
    }

    @Override
    public ResponseEntity<Void> declineOffer(Long id) {
        offerManagementService.declineOffer(id, userService.getCurrentUser());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> acceptOffer(Long id) {
        offerManagementService.acceptOffer(id, userService.getCurrentUser());
        return ResponseEntity.ok().build();
    }

    private OfferResponseTo transform(Offer offer) {
        return new OfferResponseTo()
                .id(offer.getId())
                .description(offer.getDescription())
                .price(offer.getPrice())
                .status(offer.getStatus().toString())
                .supplierId(offer.getSupplierId())
                .tenderId(offer.getTenderId());
    }
}
