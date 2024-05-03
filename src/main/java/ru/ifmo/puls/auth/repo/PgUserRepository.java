package ru.ifmo.puls.auth.repo;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.ifmo.puls.domain.User;

import static ru.ifmo.puls.auth.repo.UserMapper.ID;
import static ru.ifmo.puls.auth.repo.UserMapper.PASSWORD;
import static ru.ifmo.puls.auth.repo.UserMapper.ROLE;
import static ru.ifmo.puls.auth.repo.UserMapper.USERNAME;

@Repository
public class PgUserRepository {
    private static final String SELECT_BASE_QUERY
            = " SELECT *"
            + " FROM users";

    private static final String SELECT_BY_USERNAME_QUERY
            = SELECT_BASE_QUERY
            + " WHERE username = :username ";

    private static final String SELECT_BY_ID_QUERY
            = SELECT_BASE_QUERY
            + " WHERE " + ID + " = :" + ID;

    private static final String INSERT_WITH_RETURNING
            = " INSERT INTO users("
            + " " + USERNAME + ", "
            + " " + PASSWORD + ", "
            + " " + ROLE
            + " )"
            + " VALUES ("
            + "   :" + USERNAME + ","
            + "   :" + PASSWORD + ","
            + "   :" + ROLE
            + " )"
            + " RETURNING * ";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    public PgUserRepository(
            @Qualifier("primaryTemplate") NamedParameterJdbcTemplate jdbcTemplate,
            UserMapper userMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    public Optional<User> findByUsername(String username) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            SELECT_BY_USERNAME_QUERY,
                            Map.of(USERNAME, username),
                            userMapper
                    )
            );
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public Optional<User> findById(long id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            SELECT_BY_ID_QUERY,
                            Map.of(ID, id),
                            userMapper
                    )
            );
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public User save(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource(Map.of(
                USERNAME, user.getUsername(),
                PASSWORD, user.getPassword(),
                ROLE, user.getRole().name()
        ));

        return jdbcTemplate.queryForObject(
                INSERT_WITH_RETURNING,
                params,
                userMapper
        );
    }
}
