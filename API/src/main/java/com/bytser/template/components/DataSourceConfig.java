package com.bytser.template.components;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

@Configuration
public class DataSourceConfig {

    // Properties for WRITE datasource
    @Bean
    @ConfigurationProperties("spring.datasource.write")
    public DataSourceProperties writeDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "writeDataSource")
    public DataSource writeDataSource() {
        return writeDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    // Properties for READ datasource
    @Bean
    @ConfigurationProperties("spring.datasource.read")
    public DataSourceProperties readDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "readDataSource")
    public DataSource readDataSource() {
        return readDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    // Routing datasource: decides between READ and WRITE
    @Bean
    public DataSource routingDataSource(
            @Qualifier("writeDataSource") DataSource writeDataSource,
            @Qualifier("readDataSource") DataSource readDataSource) {

        ReadWriteRoutingDataSource routingDataSource = new ReadWriteRoutingDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.WRITE, writeDataSource);
        targetDataSources.put(DataSourceType.READ, readDataSource);

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(writeDataSource); // fallback

        return routingDataSource;
    }

    // Primary datasource used by Spring / JPA: a lazy proxy over the routing DS
    @Bean
    @Primary
    public DataSource dataSource(DataSource routingDataSource) {
        // LazyConnectionDataSourceProxy delays getting a real connection
        // until it's actually used, which plays nicely with transactions.
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}