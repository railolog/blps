package ru.ifmo.puls.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.ifmo.puls.offer.Offer;
import ru.ifmo.puls.repository.mapper.OfferMapper;

import static ru.ifmo.puls.repository.mapper.OfferMapper.DESCRIPTION;
import static ru.ifmo.puls.repository.mapper.OfferMapper.ID;
import static ru.ifmo.puls.repository.mapper.OfferMapper.PRICE;
import static ru.ifmo.puls.repository.mapper.OfferMapper.STATUS;
import static ru.ifmo.puls.repository.mapper.OfferMapper.SUPPLIER_ID;
import static ru.ifmo.puls.repository.mapper.OfferMapper.TENDER_ID;

@Repository
public class PgOfferRepository {

    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";
    private static final String ROW_COUNT = "row_count";

    private static final String SELECT_BASE_QUERY
            = " SELECT *"
            + " FROM offer";

    private static final String DELETE_BY_ID_QUERY
            = " DELETE"
            + " FROM offer"
            + " WHERE " + ID + " = :" + ID;

    private static final String SELECT_BY_ID_QUERY
            = SELECT_BASE_QUERY
            + " WHERE " + ID + " = :" + ID;

    private static final String SELECT_BY_TENDER_ID_QUERY
            = SELECT_BASE_QUERY
            + " WHERE " + TENDER_ID + " = :" + TENDER_ID;

    private static final String SELECT_BY_SUPPLIER_ID_LIMIT_QUERY
            = SELECT_BASE_QUERY
            + " WHERE " + SUPPLIER_ID + " = :" + SUPPLIER_ID
            + " ORDER BY " + ID
            + " LIMIT " + ":" + LIMIT
            + " OFFSET " + ":" + OFFSET;

    private static final String SELECT_BY_SUPPLIER_ID_TOTAL
            = " SELECT COUNT(*) AS " + ROW_COUNT
            + " FROM offer"
            + " WHERE " + SUPPLIER_ID + " = :" + SUPPLIER_ID;

    private static final String INSERT_WITH_RETURNING
            = " INSERT INTO offer("
            + " " + DESCRIPTION + ", "
            + " " + PRICE + ", "
            + " " + STATUS + ", "
            + " " + SUPPLIER_ID + ", "
            + " " + TENDER_ID
            + " )"
            + " VALUES ("
            + "   :" + DESCRIPTION + ","
            + "   :" + PRICE + ","
            + "   :" + STATUS + ","
            + "   :" + SUPPLIER_ID + ","
            + "   :" + TENDER_ID
            + " )"
            + " RETURNING * ";

    private static final String UPDATE
            = " UPDATE offer"
            + " SET "
            + STATUS + " = :" + STATUS
            + " WHERE " + ID + " = :" + ID;

    private static final String UPDATE_WITH_RETURNING
            = UPDATE
            + " RETURNING * ";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final OfferMapper offerMapper;

    public PgOfferRepository(
            @Qualifier("offerTemplate") NamedParameterJdbcTemplate jdbcTemplate,
            OfferMapper offerMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.offerMapper = offerMapper;
    }

    public List<Offer> findByTenderId(long id) {
        return jdbcTemplate.query(
                SELECT_BY_TENDER_ID_QUERY,
                Map.of(TENDER_ID, id),
                offerMapper
        );
    }

    public Optional<Offer> findById(long id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            SELECT_BY_ID_QUERY,
                            Map.of(ID, id),
                            offerMapper
                    )
            );
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Page<Offer> findBySupplierId(Pageable pageable, long id) {
        Integer total = jdbcTemplate.queryForObject(
                SELECT_BY_SUPPLIER_ID_TOTAL,
                Map.of(SUPPLIER_ID, id),
                (rs, rowNum) -> rs.getInt(ROW_COUNT)
        );

        List<Offer> offers = jdbcTemplate.query(
                SELECT_BY_SUPPLIER_ID_LIMIT_QUERY,
                Map.of(
                        SUPPLIER_ID, id,
                        LIMIT, pageable.getPageSize(),
                        OFFSET, pageable.getOffset()
                ),
                offerMapper
        );

        return new PageImpl<>(offers, pageable, total);
    }

    public void delete(Offer offer) {
        jdbcTemplate.update(
                DELETE_BY_ID_QUERY,
                Map.of(ID, offer.getId())
        );
    }

    public Offer save(Offer offer) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(DESCRIPTION, offer.getDescription());
        params.addValue(PRICE, offer.getPrice());
        params.addValue(STATUS, offer.getStatus().name());
        params.addValue(SUPPLIER_ID, offer.getSupplierId());
        params.addValue(TENDER_ID, offer.getTenderId());

        return jdbcTemplate.queryForObject(
                INSERT_WITH_RETURNING,
                params,
                offerMapper
        );
    }

    public void saveAllByTenderId(List<Offer> offers) {
        MapSqlParameterSource[] params = offers.stream()
                .map(
                        offer -> new MapSqlParameterSource()
                                .addValue(ID, offer.getId())
                                .addValue(STATUS, offer.getStatus().name())
                )
                .toArray(MapSqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(
                UPDATE,
                params
        );
    }

    public Offer update(Offer offer) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(ID, offer.getId())
                .addValue(STATUS, offer.getStatus().name());

        return jdbcTemplate.queryForObject(
                UPDATE_WITH_RETURNING,
                params,
                offerMapper
        );
    }

    public void deleteAll(List<Offer> offers) {
        MapSqlParameterSource[] params = offers.stream()
                .map(
                        offer -> new MapSqlParameterSource()
                                .addValue(ID, offer.getId())
                )
                .toArray(MapSqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(
                DELETE_BY_ID_QUERY,
                params
        );
    }
}
