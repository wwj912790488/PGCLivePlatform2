package com.arcvideo.pgcliveplatformserver.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(basePackages = {"com.arcvideo.pgcliveplatformserver.repo"})
@PropertySource("classpath:database.properties")
public class DatabaseConfig {

    @Autowired
    private Environment env;

    @Bean(destroyMethod = "close")
    DataSource dataSource() {
        HikariConfig dataSourceConfig = new HikariConfig();
        dataSourceConfig.setPoolName("springHikariCP");
        dataSourceConfig.setConnectionTestQuery("SELECT 1");
        dataSourceConfig.setDataSourceClassName(env.getProperty("hibernate.connection.driver_class"));
        Properties dataSourceProperties = new Properties();
        dataSourceProperties.put("url", env.getProperty("hibernate.connection.url"));
        dataSourceProperties.put("user", env.getProperty("hibernate.connection.username"));
        dataSourceProperties.put("password", env.getProperty("hibernate.connection.password"));
        dataSourceConfig.setDataSourceProperties(dataSourceProperties);

        return new HikariDataSource(dataSourceConfig);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setPackagesToScan(env.getProperty("entitymanager.packagesToScan"));

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);

        Properties additionalProperties = new Properties();
        additionalProperties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        additionalProperties.put("hibernate.cache.use_query_cache", env.getProperty("hibernate.cache.use_query_cache"));
        additionalProperties.put("hibernate.cache.use_second_level_cache", env.getProperty("hibernate.cache.use_second_level_cache"));
        additionalProperties.put("hibernate.cache.region.factory_class", env.getProperty("hibernate.cache.region.factory_class"));
        additionalProperties.put("hibernate.autoReconnect", env.getProperty("hibernate.autoReconnect"));
        additionalProperties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        additionalProperties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        additionalProperties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
        additionalProperties.put("javax.persistence.sharedCache.mode", env.getProperty("javax.persistence.sharedCache.mode"));
        additionalProperties.put("hibernate.generate_statistics", env.getProperty("hibernate.generate_statistics"));
        entityManagerFactory.setJpaProperties(additionalProperties);

        return entityManagerFactory;
    }



}
