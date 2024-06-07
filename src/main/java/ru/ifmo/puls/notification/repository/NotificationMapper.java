package ru.ifmo.puls.notification.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.ifmo.puls.notification.model.Notification;

@Component
public class NotificationMapper implements RowMapper<Notification> {
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String TYPE = "type";
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String VIEWED = "viewed";

    @Override
    public Notification mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Notification.builder()
                .id(rs.getLong(ID))
                .userId(rs.getLong(USER_ID))
                .title(rs.getString(TITLE))
                .message(rs.getString(MESSAGE))
                .viewed(rs.getBoolean(VIEWED))
                .build();
    }
}
