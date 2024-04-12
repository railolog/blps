package ru.ifmo.puls.service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ifmo.puls.domain.Offer;
import ru.ifmo.puls.repository.OfferRepository;

@Service
@RequiredArgsConstructor
public class OfferQueryService {
    private final OfferRepository offerRepository;

    public Optional<Offer> findById(long id) {
        return offerRepository.findById(id);
    }

    public List<Offer> findByTenderId(long tenderId) {
        return offerRepository.findByTenderId(tenderId);
    }

    public List<Offer> findBySupplierId(long supplierId) {
        return offerRepository.findBySupplierId(supplierId);
    }

    public Offer save(Offer offer) {
        return offerRepository.save(offer);
    }

    public void saveAll(Iterable<Offer> offers) {
        offerRepository.saveAll(offers);
    }
}
