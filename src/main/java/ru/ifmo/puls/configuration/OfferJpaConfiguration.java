package ru.ifmo.puls.configuration;

import java.util.Properties;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
//@EnableJpaRepositories(
//        basePackages = "ru.ifmo.puls.repository.offer",
//        entityManagerFactoryRef = "offerEntityManager",
//        enableDefaultTransactions = false
//)
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

//    @Bean
//    public LocalContainerEntityManagerFactoryBean offerEntityManager(
//            @Qualifier("offerDataSource") AtomikosDataSourceBean offerDataSource
//    ) {
//        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
//        emf.setDataSource(offerDataSource);
//        emf.setPackagesToScan("ru.ifmo.puls.offer");
//
//        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        emf.setJpaVendorAdapter(vendorAdapter);
//
//        Properties additionalProperties = new Properties();
//        additionalProperties.put("hibernate.show_sql", "true");
//        additionalProperties.put("hibernate.hbm2ddl.auto", "validate");
//        additionalProperties.put("hibernate.physical_naming_strategy",
//                CamelCaseToUnderscoresNamingStrategy.class.getName());
//        additionalProperties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
//
//        emf.setJpaProperties(additionalProperties);
//
//        return emf;
//    }

    @Bean("offerTemplate")
    public NamedParameterJdbcTemplate offerTemplate(
            @Qualifier("offerDataSource") AtomikosDataSourceBean dataSource
    ) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
