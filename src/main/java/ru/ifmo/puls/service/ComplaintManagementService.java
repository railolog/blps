package ru.ifmo.puls.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.blps.openapi.model.ComplaintResponseTo;
import ru.blps.openapi.model.ResolutionRequestTo;
import ru.ifmo.puls.domain.ComplaintConv;
import ru.ifmo.puls.domain.Resolution;
import ru.ifmo.puls.domain.Tender;
import ru.ifmo.puls.domain.TenderStatus;
import ru.ifmo.puls.exception.BadRequest;
import ru.ifmo.puls.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class ComplaintManagementService {
    private final ComplaintQueryService complaintQueryService;
    private final TenderQueryService tenderQueryService;

    public ComplaintResponseTo getById(long id) {
        ComplaintConv complaintConv = complaintQueryService.getById(id);
        Tender tender = tenderQueryService.getById(complaintConv.getTenderId());

        return enriched(complaintConv, tender);
    }

    public ComplaintResponseTo getByTenderId(long id) {
        ComplaintConv complaintConv = complaintQueryService.getByTenderId(id)
                .orElseThrow(() -> new NotFoundException("No complaints found by tender_id: " + id));
        Tender tender = tenderQueryService.getById(complaintConv.getTenderId());

        return enriched(complaintConv, tender);
    }

    @Transactional
    public void resolve(ResolutionRequestTo request) {
        ComplaintConv complaint = complaintQueryService.getById(request.getComplaintId());
        Tender tender = tenderQueryService.getById(complaint.getTenderId());

        if (request.getResolution() == ResolutionRequestTo.ResolutionEnum.ACCEPTED) {
            tender.setStatus(TenderStatus.NOT_ACCEPTED);
        } else {
            tender.setStatus(TenderStatus.ACCEPTED);
        }

        tenderQueryService.update(tender);
    }

    private ComplaintResponseTo enriched(ComplaintConv conv, Tender tender) {
        return new ComplaintResponseTo()
                .id(conv.getId())
                .message(conv.getMessage())
                .tenderId(tender.getId())
                .userId(tender.getUserId())
                .supplierId(tender.getSupplierId());
    }

    private Resolution castResolution(String resolution) {
        try {
            return Resolution.valueOf(resolution);
        } catch (IllegalArgumentException e) {
            throw new BadRequest(e.getMessage());
        }
    }
}
