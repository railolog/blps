package ru.ifmo.puls.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.ifmo.puls.domain.Offer;

public interface OfferRepository extends JpaRepository<Offer, Long>, PagingAndSortingRepository<Offer, Long> {
    List<Offer> findByTenderId(Long id);

    Page<Offer> findBySupplierId(Pageable pageable, Long id);
}
