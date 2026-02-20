# This is the application.properties file for the Example API.
# This file should be used for generating a schema.sql database schema.

spring.application.name=example
server.port=8080

# Database configuration
# Primary Database (write, consistent)
spring.datasource.write.url=jdbc:h2:mem:example;DB_CLOSE_DELAY=-1
spring.datasource.write.username=sa
spring.datasource.write.password=
spring.datasource.write.driver-class-name=org.h2.Driver
# Replicas (read-only, eventually consistent)
spring.datasource.read.url=jdbc:h2:mem:example;DB_CLOSE_DELAY=-1
spring.datasource.read.username=sa
spring.datasource.read.password=
spring.datasource.read.driver-class-name=org.h2.Driver

# Generate SQL for Postgres
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Do NOT let Hibernate touch the DB
spring.jpa.hibernate.ddl-auto=none

# Generate SQL scripts from entity metadata
spring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=create
spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=target/scheme.sql
spring.jpa.properties.jakarta.persistence.schema-generation.create-source=metadata

# Ensure Hibernate actually initializes JPA
spring.jpa.defer-datasource-initialization=true
