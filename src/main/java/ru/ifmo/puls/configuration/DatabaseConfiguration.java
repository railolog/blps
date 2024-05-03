package ru.ifmo.puls.configuration;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
@Import(FlywayConfiguration.class)
public class DatabaseConfiguration {
    @Bean(initMethod = "init", destroyMethod = "close", name = "primaryDataSource")
    @DependsOn({"flywayDelegate", "transactionManager"})
    public AtomikosDataSourceBean primaryDataSource(
            @Value("${pgaas.datasource.server}") String server,
            @Value("${pgaas.datasource.port}") String port,
            @Value("${pgaas.datasource.db}") String db,
            @Value("${pgaas.datasource.username}") String username,
            @Value("${pgaas.datasource.password}") String password,
            @Value("${atomikos.datasource.driver-class-name}") String driverName
    ) {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("primary-db");

        dataSource.setXaDataSourceClassName(driverName);

        dataSource.getXaProperties().setProperty("serverName", server);
        dataSource.getXaProperties().setProperty("portNumber", port);
        dataSource.getXaProperties().setProperty("databaseName", db);
        dataSource.getXaProperties().setProperty("user", username);
        dataSource.getXaProperties().setProperty("password", password);

        return dataSource;
    }

    @Bean("primaryTemplate")
    public NamedParameterJdbcTemplate primaryTemplate(
            @Qualifier("primaryDataSource") AtomikosDataSourceBean dataSource
    ) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
