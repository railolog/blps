package ru.ifmo.puls.notification;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.TransactionManager;

@Configuration
public class NotificationDatabaseConfiguration {
    @Bean(name = "notificationDataSource")
    public DataSource notificationDataSource(
            @Value("${notification.datasource.url}") String url,
            @Value("${notification.datasource.username}") String username,
            @Value("${notification.datasource.password}") String password
    ) {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean("notificationTemplate")
    public NamedParameterJdbcTemplate notificationTemplate(
            @Qualifier("notificationDataSource") DataSource dataSource
    ) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean("notificationTm")
    public TransactionManager notificationTm(@Qualifier("notificationDataSource") DataSource dataSource) {
        return new JdbcTransactionManager(dataSource);
    }
}
