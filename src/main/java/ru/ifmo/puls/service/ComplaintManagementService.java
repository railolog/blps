package ru.ifmo.puls.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.blps.openapi.model.ComplaintResponseTo;
import ru.blps.openapi.model.ResolutionRequestTo;
import ru.ifmo.puls.domain.ComplaintConv;
import ru.ifmo.puls.domain.Resolution;
import ru.ifmo.puls.domain.Tender;
import ru.ifmo.puls.domain.TenderStatus;
import ru.ifmo.puls.exception.BadRequest;

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

    public void resolve(ResolutionRequestTo request) {
        ComplaintConv complaint = complaintQueryService.getById(request.getComplaintId());
        Tender tender = tenderQueryService.getById(complaint.getId());

        if (request.getResolution() == ResolutionRequestTo.ResolutionEnum.ACCEPTED) {
            tender.setStatus(TenderStatus.NOT_ACCEPTED);
        } else {
            tender.setStatus(TenderStatus.ACCEPTED);
        }

        tenderQueryService.save(tender);
    }

    private ComplaintResponseTo enriched(ComplaintConv conv, Tender tender) {
        return new ComplaintResponseTo()
                .id(conv.getId())
                .type(conv.getType().toString())
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
