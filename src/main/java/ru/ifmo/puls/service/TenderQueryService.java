package ru.ifmo.puls.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.blps.openapi.model.CreateTenderRequestTo;
import ru.ifmo.puls.LimitOffsetPageRequest;
import ru.ifmo.puls.auth.model.User;
import ru.ifmo.puls.domain.Tender;
import ru.ifmo.puls.domain.TenderStatus;
import ru.ifmo.puls.dto.ListWithTotal;
import ru.ifmo.puls.exception.ConflictException;
import ru.ifmo.puls.exception.ForbiddenException;
import ru.ifmo.puls.exception.NotFoundException;
import ru.ifmo.puls.repository.TenderRepository;

@Service
@RequiredArgsConstructor
public class TenderQueryService {
    private final TenderRepository tenderRepository;

    @Transactional
    public long createTender(CreateTenderRequestTo request, User user) {
        Tender tender = Tender.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .amount(request.getAmount())
                .status(TenderStatus.NEW)
                .userId(user.getId())
                .build();

        Tender savedTender = tenderRepository.save(tender);
        return savedTender.getId();
    }

    @Transactional
    public void markFinished(long supplierId, long tenderId) {
        Tender tender = tenderRepository.findById(tenderId).orElseThrow(() -> NotFoundException.fromTender(tenderId));

        if (tender.getStatus() != TenderStatus.IN_PROGRESS) {
            throw ConflictException.incorrectTenderStatus(TenderStatus.IN_PROGRESS);
        }

        if (!Objects.equals(tender.getSupplierId(), supplierId)) {
            throw ForbiddenException.fromUserId(supplierId);
        }

        tender.setStatus(TenderStatus.FINISHED);
        tenderRepository.save(tender);
    }

    @Transactional
    public void acceptCompletion(long userId, long tenderId) {
        Tender tender = tenderRepository.findById(tenderId).orElseThrow(() -> NotFoundException.fromTender(tenderId));

        if (tender.getStatus() != TenderStatus.FINISHED) {
            throw ConflictException.incorrectTenderStatus(TenderStatus.FINISHED);
        }

        if (!Objects.equals(tender.getUserId(), userId)) {
            throw ForbiddenException.fromUserId(userId);
        }

        tender.setStatus(TenderStatus.ACCEPTED);
        tenderRepository.save(tender);
    }

    public boolean isUsersTender(long userId, long tenderId) {
        return tenderRepository.existsByIdAndUserId(tenderId, userId);
    }

    public List<Tender> findAll(int limit, int offset) {
        return tenderRepository
                .findAll(LimitOffsetPageRequest.of(limit, offset, Sort.by("id").ascending()))
                .toList();
    }

    public Optional<Tender> findById(long id) {
        return tenderRepository.findById(id);
    }

    public Tender getById(long id) {
        return tenderRepository.findById(id).orElseThrow(() -> NotFoundException.fromTender(id));
    }

    public ListWithTotal<Tender> findAllNew(int limit, int offset) {
        Page<Tender> tenders = tenderRepository
                .findByStatus(LimitOffsetPageRequest.of(limit, offset, Sort.by("id").ascending()), TenderStatus.NEW);
        return new ListWithTotal<>(tenders.stream().toList(), tenders.getTotalElements());
    }

    public ListWithTotal<Tender> findByUserId(int limit, int offset, long userId) {
        Page<Tender> tenders = tenderRepository
                .findByUserId(LimitOffsetPageRequest.of(limit, offset, Sort.by("id").ascending()), userId);
        return new ListWithTotal<>(tenders.stream().toList(), tenders.getTotalElements());
    }

    public ListWithTotal<Tender> findByStatus(int limit, int offset, TenderStatus status) {
        Page<Tender> tenders = tenderRepository
                .findByStatus(LimitOffsetPageRequest.of(limit, offset, Sort.by("id").ascending()), status);
        return new ListWithTotal<>(tenders.stream().toList(), tenders.getTotalElements());
    }

    @Transactional
    public Tender save(Tender tender) {
        return tenderRepository.save(tender);
    }
}
