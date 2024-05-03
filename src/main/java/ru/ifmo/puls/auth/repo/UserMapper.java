package ru.ifmo.puls.auth.repo;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.ifmo.puls.domain.Role;
import ru.ifmo.puls.domain.User;

@Component
public class UserMapper implements RowMapper<User> {

    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ROLE = "role";

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong(ID))
                .username(rs.getString(USERNAME))
                .password(rs.getString(PASSWORD))
                .role(Role.valueOf(rs.getString(ROLE)))
                .build();
    }
}
