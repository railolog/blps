package ru.ifmo.puls.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.blps.openapi.model.CreateOfferRequestTo;
import ru.ifmo.puls.domain.Role;
import ru.ifmo.puls.domain.User;
import ru.ifmo.puls.offer.Offer;
import ru.ifmo.puls.offer.OfferStatus;
import ru.ifmo.puls.domain.Tender;
import ru.ifmo.puls.domain.TenderStatus;
import ru.ifmo.puls.exception.ConflictException;
import ru.ifmo.puls.exception.ForbiddenException;
import ru.ifmo.puls.exception.NotFoundException;
import ru.ifmo.puls.repository.offer.OfferRepository;

@Service
@RequiredArgsConstructor
public class OfferManagementService {
    private final OfferQueryService offerQueryService;
    private final OfferRepository offerRepository;
    private final TenderQueryService tenderQueryService;

    @Transactional("myTransactionManager")
    public long createOffer(CreateOfferRequestTo request, User supplier) {
        Tender tender = tenderQueryService
                .findById(request.getTenderId())
                .orElseThrow(
                        () -> NotFoundException.fromTender(request.getTenderId())
                );

        if (tender.getStatus() != TenderStatus.NEW) {
            throw ConflictException.incorrectTenderStatus(TenderStatus.NEW);
        }

        Offer offer = Offer.builder()
                .description(request.getDescription())
                .price(request.getPrice())
                .status(OfferStatus.NEW)
                .supplierId(supplier.getId())
                .tenderId(request.getTenderId())
                .build();

        Offer savedOffer = offerQueryService.save(offer);
        return savedOffer.getId();
    }

    @Transactional("myTransactionManager")
    public void deleteOffer(long userId, long offerId) {
        Offer offer = offerQueryService.findById(offerId)
                .orElseThrow(() -> new NotFoundException("No offer with id [" + offerId + "]"));

        if (offer.getStatus() != OfferStatus.NEW) {
            throw new ConflictException("Offer must be new");
        }

        offerRepository.delete(offer);
    }

    public List<Offer> findByTenderId(long tenderId, User user) {
        if (user.getRole() == Role.USER) {
            if (!tenderQueryService.isUsersTender(user.getId(), tenderId)) {
                throw new ForbiddenException("Tender doesn't belong to the user");
            }
        }

        List<Offer> offers = offerQueryService.findByTenderId(tenderId);
        if (user.getRole() == Role.SUPPLIER) {
            return offers.stream()
                    .filter(offer -> offer.getSupplierId().equals(user.getId()))
                    .toList();
        }

        return offers;
    }

    @Transactional("myTransactionManager")
    public void declineOffer(long offerId, User user) {
        Offer offer = offerQueryService
                .findById(offerId)
                .orElseThrow(() -> new NotFoundException("Offer with id [" + offerId + "] doesn't exist"));
        Tender tender = tenderQueryService
                .findById(offer.getTenderId())
                .orElseThrow(() -> new NotFoundException("Tender for offer with id [" + offerId + "] doesn't exist"));

        if (!user.getId().equals(tender.getUserId())) {
            throw new ForbiddenException("User has no access to this tender");
        }

        if (offer.getStatus() != OfferStatus.NEW) {
            throw new ConflictException("You can decline only new offers");
        }

        offer.setStatus(OfferStatus.DECLINED);
        offerQueryService.save(offer);
    }

    @Transactional("myTransactionManager")
    public void acceptOffer(long offerId, User user) {
        Offer offer = offerQueryService
                .findById(offerId)
                .orElseThrow(() -> new NotFoundException("Offer with id [" + offerId + "] doesn't exist"));
        Tender tender = tenderQueryService
                .findById(offer.getTenderId())
                .orElseThrow(() -> new NotFoundException("Tender for offer with id [" + offerId + "] doesn't exist"));

        if (!user.getId().equals(tender.getUserId())) {
            throw new ForbiddenException("User has no access to this tender");
        }

        if (offer.getStatus() != OfferStatus.NEW) {
            throw new ConflictException("You can accept only new offers");
        }

        List<Offer> tenderOffers = offerQueryService.findByTenderId(offer.getTenderId());
        if (tenderOffers.stream().anyMatch(offer1 -> offer1.getStatus() == OfferStatus.ACCEPTED)) {
            throw new ConflictException("There are already accepted offer");
        }

        tenderOffers.forEach(offer1 -> acceptOneDeclineAll(offer1, offerId));
        offerQueryService.saveAll(tenderOffers);

        tender.setStatus(TenderStatus.IN_PROGRESS);
        tender.setSupplierId(offer.getSupplierId());
        tenderQueryService.save(tender);
    }

    private void acceptOneDeclineAll(Offer offer, long offerIdToAccept) {
        if (offer.getId().equals(offerIdToAccept)) {
            offer.setStatus(OfferStatus.ACCEPTED);
        } else {
            offer.setStatus(OfferStatus.DECLINED);
        }
    }
}
