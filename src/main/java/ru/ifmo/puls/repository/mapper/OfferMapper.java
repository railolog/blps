package ru.ifmo.puls.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.ifmo.puls.DaoTools;
import ru.ifmo.puls.domain.Offer;
import ru.ifmo.puls.domain.OfferStatus;

@Component
public class OfferMapper implements RowMapper<Offer> {
    public static final String ID = "id";
    public static final String DESCRIPTION = "description";
    public static final String PRICE = "price";
    public static final String STATUS = "status";
    public static final String TENDER_ID = "tender_id";
    public static final String SUPPLIER_ID = "supplier_id";

    @Override
    public Offer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Offer.builder()
                .id(rs.getLong(ID))
                .description(rs.getString(DESCRIPTION))
                .price(rs.getLong(PRICE))
                .status(OfferStatus.valueOf(rs.getString(STATUS)))
                .supplierId(DaoTools.getLong(rs, SUPPLIER_ID))
                .tenderId(rs.getLong(TENDER_ID))
                .build();
    }
}
