package ru.ifmo.puls.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.blps.openapi.model.CreateTenderRequestTo;
import ru.ifmo.puls.domain.ComplaintConv;
import ru.ifmo.puls.domain.Tender;
import ru.ifmo.puls.domain.TenderStatus;
import ru.ifmo.puls.exception.ConflictException;
import ru.ifmo.puls.domain.Offer;
import ru.ifmo.puls.domain.OfferStatus;
import ru.ifmo.puls.repository.PgComplaintRepository;
import ru.ifmo.puls.repository.PgOfferRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenderManagementService {
    private final TenderQueryService tenderQueryService;
    private final OfferQueryService offerQueryService;
    private final PgComplaintRepository complaintRepository;
    private final PgOfferRepository offerRepository;

    public void removeTender(long userId, long tenderId) {
        Tender tender = tenderQueryService.getById(tenderId);
        List<Offer> offers = offerQueryService.findByTenderId(tenderId);

        if (tender.getStatus() != TenderStatus.NEW) {
            throw ConflictException.incorrectTenderStatus(TenderStatus.NEW);
        }
        if (offers.stream().anyMatch(offer -> offer.getStatus() == OfferStatus.ACCEPTED)) {
            throw new ConflictException("There are accepted offers");
        }

        List<ComplaintConv> complaints = complaintRepository.findByTenderId(tenderId);
        complaintRepository.deleteAll(complaints);
        offerRepository.deleteAll(offers);
        tenderQueryService.delete(tender);
    }

    public void updateTender(long userId, long tenderId, CreateTenderRequestTo request) {
        Tender tender = tenderQueryService.getById(tenderId);

        log.error("Tender: [{}]", tender);
        if (tender.getStatus() != TenderStatus.NEW) {
            throw ConflictException.incorrectTenderStatus(TenderStatus.NEW);
        }

        tender.setTitle(request.getTitle());
        tender.setDescription(request.getDescription());
        tender.setAmount(request.getAmount());

        tenderQueryService.update(tender);
    }
}
