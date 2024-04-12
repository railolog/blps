package ru.ifmo.puls.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.ifmo.puls.domain.Tender;
import ru.ifmo.puls.domain.TenderStatus;

public interface TenderRepository extends JpaRepository<Tender, Long>, PagingAndSortingRepository<Tender, Long> {
    Page<Tender> findByStatus(Pageable pageable, TenderStatus status);

    Page<Tender> findByUserId(Pageable pageable, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}
