# This is the application.properties file for the Example API.
# This file should be used for generating a scheme.sql database scheme.

spring.application.name=example
server.port=8080

# Use H2 only to boot the app quickly; we only want metadata->DDL
spring.datasource.url=jdbc:h2:mem:example;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver

# Do NOT let Hibernate touch the DB
spring.jpa.hibernate.ddl-auto=none

# Generate SQL scripts from entity metadata
spring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=create
spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=target/schema.sql
spring.jpa.properties.jakarta.persistence.schema-generation.create-source=metadata

# Generate SQL for Postgres
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Optional: ensure Hibernate actually initializes JPA
spring.jpa.defer-datasource-initialization=true
