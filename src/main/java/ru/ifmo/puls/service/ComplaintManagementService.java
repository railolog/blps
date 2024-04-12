package ru.ifmo.puls.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.blps.openapi.model.ComplaintResponseTo;
import ru.ifmo.puls.domain.ComplaintConv;
import ru.ifmo.puls.domain.Tender;

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

    private ComplaintResponseTo enriched(ComplaintConv conv, Tender tender) {
        return new ComplaintResponseTo()
                .id(conv.getId())
                .type(conv.getType().toString())
                .message(conv.getMessage())
                .tenderId(tender.getId())
                .userId(tender.getUserId())
                .supplierId(tender.getSupplierId());
    }
}
