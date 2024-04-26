package ru.ifmo.puls.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
        basePackages = "ru.ifmo.puls.repository.offer",
        entityManagerFactoryRef = "offerEntityManager",
        transactionManagerRef = "offerTransactionManager"
)
public class OfferJpaConfiguration {
    @Bean
    public DataSource offerDataSource(
            @Value("${pgaas.offer.url}") String jdbcUrl,
            @Value("${pgaas.offer.username}") String username,
            @Value("${pgaas.offer.password}") String password,
            @Value("${spring.datasource.driver-class-name}") String driverName
    ) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverName);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean offerEntityManager(
            @Qualifier("offerDataSource") DataSource offerDataSource
    ) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(offerDataSource);
        emf.setPackagesToScan("ru.ifmo.puls.domain.offer");

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

    @Bean
    public PlatformTransactionManager offerTransactionManager(
            @Qualifier("offerEntityManager")
            LocalContainerEntityManagerFactoryBean offerEntityManager
    ) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(offerEntityManager.getObject());
        return transactionManager;
    }
}
