package ru.ifmo.puls.configuration;

import java.util.Properties;

import com.atomikos.spring.AtomikosDataSourceBean;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
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
        transactionManagerRef = "transactionManager"
)
@Import(FlywayConfiguration.class)
public class DatabaseConfiguration {
    @Bean(initMethod = "init", destroyMethod = "close")
    @DependsOn("flywayDelegate")
    public AtomikosDataSourceBean primaryDataSource(
            @Value("${pgaas.datasource.url}") String jdbcUrl,
            @Value("${pgaas.datasource.username}") String username,
            @Value("${pgaas.datasource.password}") String password,
            @Value("${atomikos.datasource.driver-class-name}") String driverName
    ) {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("primary-db");

        dataSource.setXaDataSourceClassName(driverName);

        dataSource.getXaProperties().setProperty("serverName", "db");
        dataSource.getXaProperties().setProperty("portNumber", "5432");
        dataSource.getXaProperties().setProperty("databaseName", "main");
        dataSource.getXaProperties().setProperty("user", username);
        dataSource.getXaProperties().setProperty("password", password);

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean primaryEntityManager(
            @Qualifier("primaryDataSource") AtomikosDataSourceBean dataSource
    ) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("ru.ifmo.puls.domain");

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
