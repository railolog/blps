package ru.ifmo.puls.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import com.atomikos.spring.AtomikosDataSourceBean;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@EnableJpaRepositories(
        basePackages = "ru.ifmo.puls.repository.offer",
        entityManagerFactoryRef = "offerEntityManager",
        transactionManagerRef = "transactionManager"
)
public class OfferJpaConfiguration {
    @Bean(initMethod = "init", destroyMethod = "close")
    public AtomikosDataSourceBean offerDataSource(
            @Value("${pgaas.offer.url}") String jdbcUrl,
            @Value("${pgaas.offer.username}") String username,
            @Value("${pgaas.offer.password}") String password,
            @Value("${atomikos.datasource.driver-class-name}") String driverName
    ) {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("offer-db");

        dataSource.setXaDataSourceClassName(driverName);

        dataSource.getXaProperties().setProperty("serverName", "offer_db");
        dataSource.getXaProperties().setProperty("portNumber", "5432");
        dataSource.getXaProperties().setProperty("databaseName", "secondary");
        dataSource.getXaProperties().setProperty("user", username);
        dataSource.getXaProperties().setProperty("password", password);

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean offerEntityManager(
            @Qualifier("offerDataSource") AtomikosDataSourceBean offerDataSource
    ) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(offerDataSource);
        emf.setPackagesToScan("ru.ifmo.puls.offer");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);

        Properties additionalProperties = new Properties();
        additionalProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        additionalProperties.put("hibernate.show_sql", "true");
        additionalProperties.put("hibernate.hbm2ddl.auto", "validate");
        additionalProperties.put("hibernate.physical_naming_strategy",
                CamelCaseToUnderscoresNamingStrategy.class.getName());
        additionalProperties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());

        emf.setJpaProperties(additionalProperties);

        return emf;
    }
}
