package ru.ifmo.puls.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.ifmo.puls.DaoTools;
import ru.ifmo.puls.domain.Tender;
import ru.ifmo.puls.domain.TenderStatus;

@Component
public class TenderMapper implements RowMapper<Tender> {
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String AMOUNT = "amount";
    public static final String STATUS = "status";
    public static final String USER_ID = "user_id";
    public static final String SUPPLIER_ID = "supplier_id";

    @Override
    public Tender mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Tender.builder()
                .id(rs.getLong(ID))
                .title(rs.getString(TITLE))
                .description(rs.getString(DESCRIPTION))
                .amount(rs.getLong(AMOUNT))
                .status(TenderStatus.valueOf(rs.getString(STATUS)))
                .userId(rs.getLong(USER_ID))
                .supplierId(DaoTools.getLong(rs, SUPPLIER_ID))
                .build();
    }
}
