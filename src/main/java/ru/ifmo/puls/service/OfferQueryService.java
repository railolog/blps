package ru.ifmo.puls.service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ifmo.puls.LimitOffsetPageRequest;
import ru.ifmo.puls.dto.ListWithTotal;
import ru.ifmo.puls.domain.Offer;
import ru.ifmo.puls.repository.PgOfferRepository;

@Service
@RequiredArgsConstructor
public class OfferQueryService {
    private final PgOfferRepository offerRepository;

    public Optional<Offer> findById(long id) {
        return offerRepository.findById(id);
    }

    public List<Offer> findByTenderId(long tenderId) {
        return offerRepository.findByTenderId(tenderId);
    }

    public ListWithTotal<Offer> findBySupplierId(int limit, int offset, long supplierId) {
        Page<Offer> offers = offerRepository
                .findBySupplierId(LimitOffsetPageRequest.of(limit, offset, Sort.by("id").ascending()), supplierId);
        return new ListWithTotal<>(offers.stream().toList(), offers.getTotalElements());
    }

    @Transactional
    public Offer save(Offer offer) {
        return offerRepository.save(offer);
    }

    @Transactional
    public Offer update(Offer offer) {
        return offerRepository.update(offer);
    }

    @Transactional
    public void saveAllByTenderId(List<Offer> offers) {
        offerRepository.saveAllByTenderId(offers);
    }
}
