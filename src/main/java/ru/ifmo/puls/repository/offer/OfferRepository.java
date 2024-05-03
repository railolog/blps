package ru.ifmo.puls.repository.offer;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.ifmo.puls.offer.Offer;

public interface OfferRepository { //extends JpaRepository<Offer, Long>, PagingAndSortingRepository<Offer, Long> {
    List<Offer> findByTenderId(long id);

    Page<Offer> findBySupplierId(Pageable pageable, long id);

    void delete(Offer offer);

    Optional<Offer> findById(long id);

    Offer save(Offer offer);

    void saveAllByTenderId(List<Offer> offers);

    Offer update(Offer offer);

    void deleteAll(List<Offer> offers);
}
