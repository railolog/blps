package ru.ifmo.puls.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.ifmo.puls.domain.ComplaintConv;

@Component
public class ComplaintMapper implements RowMapper<ComplaintConv> {
    public static final String ID = "id";
    public static final String MESSAGE = "message";
    public static final String TENDER_ID = "tender_id";

    @Override
    public ComplaintConv mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ComplaintConv.builder()
                .id(rs.getLong(ID))
                .message(rs.getString(MESSAGE))
                .tenderId(rs.getLong(TENDER_ID))
                .build();
    }
}
