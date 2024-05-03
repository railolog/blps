package ru.ifmo.puls;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DaoTools {
    public static Integer getInteger(ResultSet rs, String strColName) throws SQLException {
        int nValue = rs.getInt(strColName);
        return rs.wasNull() ? null : nValue;
    }

    public static Long getLong(ResultSet rs, String strColName) throws SQLException {
        long nValue = rs.getLong(strColName);
        return rs.wasNull() ? null : nValue;
    }
}
