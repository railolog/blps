package ru.ifmo.puls.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ifmo.puls.domain.ComplaintConv;
import ru.ifmo.puls.domain.ConvType;
import ru.ifmo.puls.domain.Tender;
import ru.ifmo.puls.domain.TenderStatus;
import ru.ifmo.puls.exception.ConflictException;
import ru.ifmo.puls.exception.ForbiddenException;
import ru.ifmo.puls.exception.NotFoundException;
import ru.ifmo.puls.repository.ComplaintRepository;

@Service
@RequiredArgsConstructor
public class ComplaintQueryService {
    private final ComplaintRepository complaintRepository;
    private final TenderQueryService tenderQueryService;

    @Transactional
    public ComplaintConv create(long userId, long tenderId, String message) {
        Tender tender = tenderQueryService.findById(tenderId).orElseThrow(() -> NotFoundException.fromTender(tenderId));

        if (!tender.getUserId().equals(userId)) {
            throw ForbiddenException.fromUserId(userId);
        }

        if (tender.getStatus() != TenderStatus.FINISHED) {
            throw ConflictException.incorrectTenderStatus(TenderStatus.FINISHED);
        }

        ComplaintConv complaint = ComplaintConv.builder()
                .type(ConvType.COMPLAINT)
                .message(message)
                .tenderId(tenderId)
                .build();

        tender.setStatus(TenderStatus.IN_DISPUTE);
        tenderQueryService.save(tender);

        return complaintRepository.save(complaint);
    }

    public ComplaintConv getById(long id) {
        return complaintRepository.findById(id).orElseThrow(() -> NotFoundException.fromComplaint(id));
    }
}
