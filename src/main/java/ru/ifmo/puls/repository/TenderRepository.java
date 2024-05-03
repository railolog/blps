package ru.ifmo.puls.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.ifmo.puls.LimitOffsetPageRequest;
import ru.ifmo.puls.domain.Tender;
import ru.ifmo.puls.domain.TenderStatus;

public interface TenderRepository { // extends JpaRepository<Tender, Long>, PagingAndSortingRepository<Tender, Long> {
    Page<Tender> findByStatus(Pageable pageable, TenderStatus status);

    Page<Tender> findByUserId(Pageable pageable, long userId);

    boolean existsByIdAndUserId(long id, long userId);

    long countByStatusAndUserId(TenderStatus status, long userId);

    long countByStatusAndSupplierId(TenderStatus status, long supplierId);

    Optional<Tender> findById(long id);

    Page<Tender> findAll(LimitOffsetPageRequest pageable);

    Tender save(Tender tender);

    void delete(Tender tender);

    Tender update(Tender tender);
}
