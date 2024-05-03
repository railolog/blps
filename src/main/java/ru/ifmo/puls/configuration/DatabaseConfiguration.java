package ru.ifmo.puls.configuration;

import java.util.Properties;

import com.atomikos.jdbc.AtomikosDataSourceBean;
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import ru.ifmo.puls.repository.PgTenderRepository;

@Slf4j
@Configuration
@EnableJpaRepositories(
        basePackages = "ru.ifmo.puls",
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {PgTenderRepository.class}
                )
        },
        entityManagerFactoryRef = "primaryEntityManager",
        enableDefaultTransactions = false
)
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
        additionalProperties.put("hibernate.show_sql", "true");
        additionalProperties.put("hibernate.hbm2ddl.auto", "validate");
        additionalProperties.put("hibernate.physical_naming_strategy",
                CamelCaseToUnderscoresNamingStrategy.class.getName());
        additionalProperties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());

        emf.setJpaProperties(additionalProperties);

        return emf;
    }

    @Bean("primaryTemplate")
    public NamedParameterJdbcTemplate primaryTemplate(
            @Qualifier("primaryDataSource") AtomikosDataSourceBean dataSource
    ) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
