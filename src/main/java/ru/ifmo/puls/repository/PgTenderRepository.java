package ru.ifmo.puls.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.ifmo.puls.LimitOffsetPageRequest;
import ru.ifmo.puls.domain.Tender;
import ru.ifmo.puls.domain.TenderStatus;
import ru.ifmo.puls.repository.mapper.TenderMapper;

import static ru.ifmo.puls.repository.mapper.TenderMapper.AMOUNT;
import static ru.ifmo.puls.repository.mapper.TenderMapper.DESCRIPTION;
import static ru.ifmo.puls.repository.mapper.TenderMapper.ID;
import static ru.ifmo.puls.repository.mapper.TenderMapper.STATUS;
import static ru.ifmo.puls.repository.mapper.TenderMapper.SUPPLIER_ID;
import static ru.ifmo.puls.repository.mapper.TenderMapper.TITLE;
import static ru.ifmo.puls.repository.mapper.TenderMapper.USER_ID;

@Repository
public class PgTenderRepository implements TenderRepository {

    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";
    private static final String ROW_COUNT = "row_count";

    private static final String SELECT_BASE_QUERY
            = " SELECT *"
            + " FROM tender";

    private static final String DELETE_BY_ID_QUERY
            = " DELETE"
            + " FROM tender"
            + " WHERE " + ID + " = :" + ID;

    private static final String SELECT_BASE_LIMIT_QUERY
            = " SELECT *"
            + " FROM tender"
            + " ORDER BY " + ID
            + " LIMIT " + ":" + LIMIT
            + " OFFSET " + ":" + OFFSET;

    private static final String SELECT_BASE_TOTAL
            = " SELECT COUNT(*) AS " + ROW_COUNT
            + " FROM tender";

    private static final String SELECT_BY_ID_QUERY
            = SELECT_BASE_QUERY
            + " WHERE " + ID + " = :" + ID;

    private static final String SELECT_BY_USER_ID_LIMIT_QUERY
            = SELECT_BASE_QUERY
            + " WHERE " + USER_ID + " = :" + USER_ID
            + " ORDER BY " + ID
            + " LIMIT " + ":" + LIMIT
            + " OFFSET " + ":" + OFFSET;

    private static final String SELECT_BY_USER_ID_TOTAL
            = " SELECT COUNT(*) AS " + ROW_COUNT
            + " FROM tender"
            + " WHERE " + USER_ID + " = :" + USER_ID;

    private static final String SELECT_BY_USER_ID_QUERY
            = SELECT_BASE_QUERY
            + " WHERE " + USER_ID + " = :" + USER_ID;

    private static final String SELECT_BY_SUPPLIER_ID_QUERY
            = SELECT_BASE_QUERY
            + " WHERE " + SUPPLIER_ID + " = :" + SUPPLIER_ID;

    private static final String SELECT_BY_STATUS_LIMIT_QUERY
            = SELECT_BASE_QUERY
            + " WHERE " + STATUS + " = :" + STATUS
            + " ORDER BY " + ID
            + " LIMIT " + ":" + LIMIT
            + " OFFSET " + ":" + OFFSET;

    private static final String SELECT_BY_STATUS_TOTAL
            = " SELECT COUNT(*) AS " + ROW_COUNT
            + " FROM tender"
            + " WHERE " + STATUS + " = :" + STATUS;

    private static final String INSERT_WITH_RETURNING
            = " INSERT INTO tender("
            + " " + TITLE + ", "
            + " " + DESCRIPTION + ", "
            + " " + AMOUNT + ", "
            + " " + STATUS + ", "
            + " " + USER_ID + ", "
            + " " + SUPPLIER_ID
            + " )"
            + " VALUES ("
            + "   :" + TITLE + ","
            + "   :" + DESCRIPTION + ","
            + "   :" + AMOUNT + ","
            + "   :" + STATUS + ","
            + "   :" + USER_ID + ","
            + "   :" + SUPPLIER_ID
            + " )"
            + " RETURNING * ";

    private static final String UPDATE
            = " UPDATE tender"
            + " SET "
            + TITLE + " = :" + TITLE + ", "
            + DESCRIPTION + " = :" + DESCRIPTION + ", "
            + AMOUNT + " = :" + AMOUNT + ", "
            + STATUS + " = :" + STATUS + ", "
            + SUPPLIER_ID + " = :" + SUPPLIER_ID
            + " WHERE " + ID + " = :" + ID;

    private static final String UPDATE_WITH_RETURNING
            = UPDATE
            + " RETURNING * ";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TenderMapper tenderMapper;

    public PgTenderRepository(
            @Qualifier("primaryTemplate") NamedParameterJdbcTemplate jdbcTemplate,
            TenderMapper tenderMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.tenderMapper = tenderMapper;
    }

    @Override
    public Page<Tender> findByStatus(Pageable pageable, TenderStatus status) {
        Integer total = jdbcTemplate.queryForObject(
                SELECT_BY_STATUS_TOTAL,
                Map.of(STATUS, status.name()),
                (rs, rowNum) -> rs.getInt(ROW_COUNT)
        );

        List<Tender> tenders = jdbcTemplate.query(
                SELECT_BY_STATUS_LIMIT_QUERY,
                Map.of(
                        STATUS, status.name(),
                        LIMIT, pageable.getPageSize(),
                        OFFSET, pageable.getOffset()
                ),
                tenderMapper
        );

        return new PageImpl<>(tenders, pageable, total);
    }

    @Override
    public Page<Tender> findByUserId(Pageable pageable, long userId) {
        Integer total = jdbcTemplate.queryForObject(
                SELECT_BY_USER_ID_TOTAL,
                Map.of(USER_ID, userId),
                (rs, rowNum) -> rs.getInt(ROW_COUNT)
        );

        List<Tender> tenders = jdbcTemplate.query(
                SELECT_BY_USER_ID_LIMIT_QUERY,
                Map.of(
                        USER_ID, userId,
                        LIMIT, pageable.getPageSize(),
                        OFFSET, pageable.getOffset()
                ),
                tenderMapper
        );

        return new PageImpl<>(tenders, pageable, total);
    }

    @Override
    public boolean existsByIdAndUserId(long id, long userId) {
        return findByUserId(userId).stream()
                .anyMatch(tender -> Objects.equals(id, tender.getId()));
    }

    @Override
    public long countByStatusAndUserId(TenderStatus status, long userId) {
        return findByUserId(userId).stream()
                .filter(tender -> Objects.equals(status, tender.getStatus()))
                .count();
    }

    @Override
    public long countByStatusAndSupplierId(TenderStatus status, long supplierId) {
        return findBySupplierId(supplierId).stream()
                .filter(tender -> Objects.equals(status, tender.getStatus()))
                .count();
    }

    @Override
    public Optional<Tender> findById(long id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            SELECT_BY_ID_QUERY,
                            Map.of(ID, id),
                            tenderMapper
                    )
            );
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Page<Tender> findAll(LimitOffsetPageRequest pageable) {
        Integer total = jdbcTemplate.queryForObject(
                SELECT_BASE_TOTAL,
                Map.of(),
                (rs, rowNum) -> rs.getInt(ROW_COUNT)
        );

        List<Tender> tenders = jdbcTemplate.query(
                SELECT_BASE_LIMIT_QUERY,
                Map.of(
                        LIMIT, pageable.getPageSize(),
                        OFFSET, pageable.getOffset()
                ),
                tenderMapper
        );

        return new PageImpl<>(tenders, pageable, total);
    }

    @Override
    public Tender save(Tender tender) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(TITLE, tender.getTitle());
        params.addValue(DESCRIPTION, tender.getDescription());
        params.addValue(AMOUNT, tender.getAmount());
        params.addValue(STATUS, tender.getStatus().name());
        params.addValue(USER_ID, tender.getUserId());
        params.addValue(SUPPLIER_ID, tender.getSupplierId());

        return jdbcTemplate.queryForObject(
                INSERT_WITH_RETURNING,
                params,
                tenderMapper
        );
    }

    @Override
    public void delete(Tender tender) {
        jdbcTemplate.update(
                DELETE_BY_ID_QUERY,
                Map.of(ID, tender.getId())
        );
    }

    @Override
    public Tender update(Tender tender) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(TITLE, tender.getTitle());
        params.addValue(DESCRIPTION, tender.getDescription());
        params.addValue(AMOUNT, tender.getAmount());
        params.addValue(STATUS, tender.getStatus().name());
        params.addValue(SUPPLIER_ID, tender.getSupplierId());
        params.addValue(ID, tender.getId());

        return jdbcTemplate.queryForObject(
                UPDATE_WITH_RETURNING,
                params,
                tenderMapper
        );
    }

    private List<Tender> findByUserId(long userId) {
        return jdbcTemplate.query(
                SELECT_BY_USER_ID_QUERY,
                Map.of(
                        USER_ID, userId
                ),
                tenderMapper
        );
    }

    private List<Tender> findBySupplierId(long supplierId) {
        return jdbcTemplate.query(
                SELECT_BY_SUPPLIER_ID_QUERY,
                Map.of(
                        SUPPLIER_ID, supplierId
                ),
                tenderMapper
        );
    }
}
