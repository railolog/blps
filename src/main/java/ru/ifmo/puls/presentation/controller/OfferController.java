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
import ru.blps.openapi.model.OfferShortResponseTo;
import ru.ifmo.puls.domain.User;
import ru.ifmo.puls.auth.service.UserService;
import ru.ifmo.puls.domain.Offer;
import ru.ifmo.puls.dto.ListWithTotal;
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
    public ResponseEntity<Void> deleteOffer(Long id) {
        User user = userService.getCurrentUser();
        offerManagementService.deleteOffer(user.getId(), id);
        return ResponseEntity.ok().build();
    }

    @Override
    @Secured("SUPPLIER")
    public ResponseEntity<OfferListResponseTo> getSupplierOffers(Integer limit, Integer offset) {
        return ResponseEntity.ok(
                transform(
                        offerQueryService.findBySupplierId(limit, offset, userService.getCurrentUser().getId())
                )
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

    private OfferListResponseTo transform(ListWithTotal<Offer> offers) {
        return new OfferListResponseTo()
                .offers(
                        offers.items().stream()
                                .map(this::transformShort)
                                .toList()
                )
                .total(offers.total());
    }

    private OfferListResponseTo transform(List<Offer> offers) {
        return new OfferListResponseTo()
                .offers(
                        offers.stream()
                                .map(this::transformShort)
                                .toList()
                );
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

    private OfferShortResponseTo transformShort(Offer offer) {
        return new OfferShortResponseTo()
                .id(offer.getId())
                .price(offer.getPrice())
                .status(offer.getStatus().toString())
                .supplierId(offer.getSupplierId())
                .tenderId(offer.getTenderId());
    }
}
