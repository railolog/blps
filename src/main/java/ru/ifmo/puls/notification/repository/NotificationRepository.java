package ru.ifmo.puls.notification.repository;

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
import org.springframework.transaction.annotation.Transactional;
import ru.ifmo.puls.common.NotificationMsgDto;
import ru.ifmo.puls.notification.model.Notification;
import ru.ifmo.puls.notification.model.NotificationAmount;

import static ru.ifmo.puls.notification.repository.NotificationMapper.ID;
import static ru.ifmo.puls.notification.repository.NotificationMapper.MESSAGE;
import static ru.ifmo.puls.notification.repository.NotificationMapper.TITLE;
import static ru.ifmo.puls.notification.repository.NotificationMapper.USER_ID;
import static ru.ifmo.puls.notification.repository.NotificationMapper.VIEWED;


@Repository
public class NotificationRepository {
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";
    private static final String ROW_COUNT = "row_count";

    private static final String SELECT_BASE_QUERY
            = " SELECT *"
            + " FROM notification";

    private static final String SELECT_BY_ID_QUERY
            = SELECT_BASE_QUERY
            + " WHERE " + ID + " = :" + ID;

    private static final String SELECT_BY_USER_ID_QUERY
            = SELECT_BASE_QUERY
            + " WHERE " + USER_ID + " = :" + USER_ID
            + " ORDER BY " + ID + " DESC"
            + " LIMIT " + ":" + LIMIT
            + " OFFSET " + ":" + OFFSET;

    private static final String COUNT_BY_USER_ID_QUERY
            = " SELECT COUNT(*) AS " + ROW_COUNT
            + " FROM notification"
            + " WHERE " + USER_ID + " = :" + USER_ID;

    private static final String COUNT_BY_USER_ID_NOT_VIEWED
            = " SELECT COUNT(*) AS " + ROW_COUNT
            + " FROM notification"
            + " WHERE " + USER_ID + " = :" + USER_ID
            + " AND viewed=false";

    private static final String INSERT_WITH_RETURNING
            = " INSERT INTO notification("
            + " " + USER_ID + ", "
            + " " + TITLE + ", "
            + " " + MESSAGE + ", "
            + " " + VIEWED
            + " )"
            + " VALUES ("
            + "   :" + USER_ID + ","
            + "   :" + TITLE + ","
            + "   :" + MESSAGE + ","
            + "   :" + VIEWED
            + " )"
            + " RETURNING * ";

    private static final String UPDATE
            = " UPDATE notification"
            + " SET "
            + VIEWED + " = :" + VIEWED
            + " WHERE " + ID + " = :" + ID;

    private static final String UPDATE_NOT_VIEWED
            = " UPDATE notification"
            + " SET viewed=true"
            + " WHERE viewed=false AND " + USER_ID + " = :" + USER_ID;

    private static final String UPDATE_WITH_RETURNING
            = UPDATE
            + " RETURNING * ";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final NotificationMapper notificationMapper;

    public NotificationRepository(
            @Qualifier("notificationTemplate") NamedParameterJdbcTemplate jdbcTemplate,
            NotificationMapper notificationMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.notificationMapper = notificationMapper;
    }

    public Optional<Notification> findById(long id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            SELECT_BY_ID_QUERY,
                            Map.of(ID, id),
                            notificationMapper
                    )
            );
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Page<Notification> findByUserId(Pageable pageable, long userId) {
        Integer total = getTotalByUserId(userId);

        List<Notification> notifications = jdbcTemplate.query(
                SELECT_BY_USER_ID_QUERY,
                Map.of(
                        USER_ID, userId,
                        LIMIT, pageable.getPageSize(),
                        OFFSET, pageable.getOffset()
                ),
                notificationMapper
        );

        return new PageImpl<>(notifications, pageable, total);
    }

    public Integer getTotalByUserId(long userId) {
        return jdbcTemplate.queryForObject(
                COUNT_BY_USER_ID_QUERY,
                Map.of(USER_ID, userId),
                (rs, rowNum) -> rs.getInt(ROW_COUNT)
        );
    }

    public NotificationAmount getAmountByUserId(long userId) {
        Integer notViewed = jdbcTemplate.queryForObject(
                COUNT_BY_USER_ID_NOT_VIEWED,
                Map.of(USER_ID, userId),
                (rs, rowNum) -> rs.getInt(ROW_COUNT)
        );

        return new NotificationAmount(getTotalByUserId(userId), notViewed);
    }

    @Transactional("notificationTm")
    public Notification create(NotificationMsgDto notification) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, notification.recipientId());
        params.addValue(TITLE, notification.title());
        params.addValue(MESSAGE, notification.message());
        params.addValue(VIEWED, false);

        return jdbcTemplate.queryForObject(
                INSERT_WITH_RETURNING,
                params,
                notificationMapper
        );
    }

    @Transactional("notificationTm")
    public Notification update(Notification notification) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, notification.getId());
        params.addValue(VIEWED, notification.getViewed());

        return jdbcTemplate.queryForObject(
                UPDATE_WITH_RETURNING,
                params,
                notificationMapper
        );
    }

    @Transactional("notificationTm")
    public void markViewedByUserId(long userId) {
        jdbcTemplate.update(
                UPDATE_NOT_VIEWED,
                Map.of(USER_ID, userId)
        );
    }
}
