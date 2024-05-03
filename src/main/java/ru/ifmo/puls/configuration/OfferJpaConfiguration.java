package ru.ifmo.puls.configuration;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class OfferJpaConfiguration {
    @Bean("offerDataSource")
    @DependsOn("transactionManager")
    public AtomikosDataSourceBean offerDataSource(
            @Value("${pgaas.offer.server}") String server,
            @Value("${pgaas.offer.port}") String port,
            @Value("${pgaas.offer.db}") String db,
            @Value("${pgaas.offer.username}") String username,
            @Value("${pgaas.offer.password}") String password,
            @Value("${atomikos.datasource.driver-class-name}") String driverName
    ) {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("offer-db");

        dataSource.setXaDataSourceClassName(driverName);

        dataSource.getXaProperties().setProperty("serverName", server);
        dataSource.getXaProperties().setProperty("portNumber", port);
        dataSource.getXaProperties().setProperty("databaseName", db);
        dataSource.getXaProperties().setProperty("user", username);
        dataSource.getXaProperties().setProperty("password", password);

        return dataSource;
    }

    @Bean("offerTemplate")
    public NamedParameterJdbcTemplate offerTemplate(
            @Qualifier("offerDataSource") AtomikosDataSourceBean dataSource
    ) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
