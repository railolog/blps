package ru.ifmo.puls.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import ru.ifmo.puls.repository.offer.OfferRepository;

@Slf4j
@Configuration
@EnableJpaRepositories(
        basePackages = "ru.ifmo.puls",
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = OfferRepository.class
                )
        },
        entityManagerFactoryRef = "primaryEntityManager",
        transactionManagerRef = "primaryTransactionManager"
)
@Import(FlywayConfiguration.class)
public class DatabaseConfiguration {
    @Primary
    @Bean
    @DependsOn("flywayDelegate")
    public DataSource dataSource(
            @Value("${pgaas.datasource.url}") String jdbcUrl,
            @Value("${pgaas.datasource.username}") String username,
            @Value("${pgaas.datasource.password}") String password,
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
    @Primary
    public LocalContainerEntityManagerFactoryBean primaryEntityManager(
            DataSource dataSource
    ) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("ru.ifmo.puls");

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
    @Primary
    public PlatformTransactionManager primaryTransactionManager(
            LocalContainerEntityManagerFactoryBean primaryEntityManager
    ) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(primaryEntityManager.getObject());
        return transactionManager;
    }
}
