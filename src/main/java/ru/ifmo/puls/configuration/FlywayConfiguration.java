package ru.ifmo.puls.configuration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.MethodInvokingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class FlywayConfiguration {
    @Bean
    public DriverManagerDataSource flywayDataSource(
            @Value("${pgaas.datasource.url}") String jdbcUrl,
            @Value("${pgaas.datasource.username}") String username,
            @Value("${pgaas.datasource.password}") String password
    ) {
        return new DriverManagerDataSource(jdbcUrl, username, password);
    }

    @Bean
    public Flyway flyway(
            DriverManagerDataSource flywayDataSource,
            @Value("${flyway.ignore.missed.migration:false}") boolean ignoreMissedMigration
    ) {
        ClassicConfiguration flywayConfiguration = new ClassicConfiguration();
        flywayConfiguration.setDataSource(flywayDataSource);
        flywayConfiguration.setBaselineOnMigrate(true);
        flywayConfiguration.setValidateMigrationNaming(false);

        return new Flyway(flywayConfiguration);
    }

    @Bean
    public MethodInvokingBean flywayDelegate(
            Flyway flyway
    ) {
        MethodInvokingBean invokingBean = new MethodInvokingBean();
        invokingBean.setTargetObject(flyway);
        invokingBean.setTargetMethod("migrate");

        return invokingBean;
    }
}
