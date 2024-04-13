package ru.ifmo.puls.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;
import ru.blps.openapi.api.TenderApi;
import ru.blps.openapi.model.CreateTenderRequestTo;
import ru.blps.openapi.model.CreateTenderResponseTo;
import ru.blps.openapi.model.TenderListResponseTo;
import ru.blps.openapi.model.TenderResponseTo;
import ru.blps.openapi.model.TenderShortResponseTo;
import ru.ifmo.puls.auth.model.User;
import ru.ifmo.puls.auth.service.UserService;
import ru.ifmo.puls.domain.Tender;
import ru.ifmo.puls.domain.TenderStatus;
import ru.ifmo.puls.dto.ListWithTotal;
import ru.ifmo.puls.exception.BadRequest;
import ru.ifmo.puls.service.TenderManagementService;
import ru.ifmo.puls.service.TenderQueryService;

@RestController
@RequiredArgsConstructor
public class TenderController implements TenderApi {
    private final TenderQueryService tenderQueryService;
    private final TenderManagementService tenderManagementService;
    private final UserService userService;

    @Override
    public ResponseEntity<TenderListResponseTo> getTenders(Integer limit, Integer offset) {
        ListWithTotal<Tender> tenders = tenderQueryService.findAllNew(limit, offset);
        return ResponseEntity.ok(
                transform(tenders)
        );
    }

    @Override
    public ResponseEntity<TenderResponseTo> getTenderById(Long id) {
        return tenderQueryService.findById(id)
                .map(tender -> ResponseEntity.ok(transform(tender)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<TenderListResponseTo> getTendersByStatus(Integer limit, Integer offset, String status) {
        return ResponseEntity.ok(
                transform(tenderQueryService.findByStatus(limit, offset, castStatus(status)))
        );
    }

    @Override
    @Secured("USER")
    public ResponseEntity<TenderListResponseTo> getMyTenders(Integer limit, Integer offset) {
        User user = userService.getCurrentUser();
        ListWithTotal<Tender> tenders = tenderQueryService.findByUserId(limit, offset, user.getId());
        return ResponseEntity.ok(
                transform(tenders)
        );
    }

    @Override
    @Secured("USER")
    public ResponseEntity<CreateTenderResponseTo> createTender(CreateTenderRequestTo createTenderRequestTo) {
        User user = userService.getCurrentUser();

        return ResponseEntity.ok(
                new CreateTenderResponseTo()
                        .id(tenderQueryService.createTender(createTenderRequestTo, user))
        );
    }

    @Override
    @Secured("SUPPLIER")
    public ResponseEntity<Void> finishTender(Long tenderId) {
        User user = userService.getCurrentUser();
        tenderQueryService.markFinished(user.getId(), tenderId);
        return ResponseEntity.ok().build();
    }

    @Override
    @Secured("USER")
    public ResponseEntity<Void> acceptCompletion(Long tenderId) {
        User user = userService.getCurrentUser();
        tenderQueryService.acceptCompletion(user.getId(), tenderId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteTender(Long id) {
        User user = userService.getCurrentUser();
        tenderManagementService.removeTender(user.getId(), id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> updateTender(Long id, CreateTenderRequestTo createTenderRequestTo) {
        User user = userService.getCurrentUser();
        tenderManagementService.updateTender(user.getId(), id, createTenderRequestTo);
        return ResponseEntity.ok().build();
    }

    private TenderListResponseTo transform(ListWithTotal<Tender> tenders) {
        return new TenderListResponseTo()
                .total(tenders.total())
                .tenders(
                        tenders.items()
                                .stream()
                                .map(this::transformShort)
                                .toList()
                );
    }

    private TenderResponseTo transform(Tender tender) {
        return new TenderResponseTo()
                .id(tender.getId())
                .title(tender.getTitle())
                .description(tender.getDescription())
                .status(tender.getStatus().toString())
                .amount(tender.getAmount());
    }

    private TenderShortResponseTo transformShort(Tender tender) {
        return new TenderShortResponseTo()
                .id(tender.getId())
                .title(tender.getTitle())
                .status(tender.getStatus().toString())
                .amount(tender.getAmount());
    }

    private TenderStatus castStatus(String status) {
        try {
            return TenderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequest(e.getMessage());
        }
    }
}
