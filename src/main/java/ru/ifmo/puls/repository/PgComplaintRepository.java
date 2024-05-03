package ru.ifmo.puls.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ifmo.puls.domain.ComplaintConv;
import ru.ifmo.puls.repository.mapper.ComplaintMapper;

import static ru.ifmo.puls.repository.mapper.ComplaintMapper.ID;
import static ru.ifmo.puls.repository.mapper.ComplaintMapper.MESSAGE;
import static ru.ifmo.puls.repository.mapper.ComplaintMapper.TENDER_ID;

@Repository
public class PgComplaintRepository {

    private static final String SELECT_BASE_QUERY
            = " SELECT *"
            + " FROM complaint";

    private static final String DELETE_BY_ID_QUERY
            = " DELETE"
            + " FROM complaint"
            + " WHERE " + ID + " = :" + ID;

    private static final String SELECT_BY_ID_QUERY
            = SELECT_BASE_QUERY
            + " WHERE " + ID + " = :" + ID;

    private static final String SELECT_BY_TENDER_ID_QUERY
            = SELECT_BASE_QUERY
            + " WHERE " + TENDER_ID + " = :" + TENDER_ID;

    private static final String INSERT_WITH_RETURNING
            = " INSERT INTO complaint("
            + " " + MESSAGE + ", "
            + " " + TENDER_ID
            + " )"
            + " VALUES ("
            + "   :" + MESSAGE + ","
            + "   :" + TENDER_ID
            + " )"
            + " RETURNING * ";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ComplaintMapper complaintMapper;

    public PgComplaintRepository(
            @Qualifier("primaryTemplate") NamedParameterJdbcTemplate jdbcTemplate,
            ComplaintMapper complaintMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.complaintMapper = complaintMapper;
    }

    public List<ComplaintConv> findByTenderId(long tenderId) {
        return jdbcTemplate.query(
                SELECT_BY_TENDER_ID_QUERY,
                Map.of(TENDER_ID, tenderId),
                complaintMapper
        );
    }

    public Optional<ComplaintConv> findById(long id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            SELECT_BY_ID_QUERY,
                            Map.of(ID, id),
                            complaintMapper
                    )
            );
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Transactional
    public ComplaintConv save(ComplaintConv complaint) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(MESSAGE, complaint.getMessage())
                .addValue(TENDER_ID, complaint.getTenderId());

        return jdbcTemplate.queryForObject(
                INSERT_WITH_RETURNING,
                params,
                complaintMapper
        );
    }

    @Transactional
    public void deleteAll(List<ComplaintConv> complaints) {
        MapSqlParameterSource[] params = complaints.stream()
                .map(complaint -> new MapSqlParameterSource(ID, complaint.getId()))
                .toArray(MapSqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(
                DELETE_BY_ID_QUERY,
                params
        );
    }
}
