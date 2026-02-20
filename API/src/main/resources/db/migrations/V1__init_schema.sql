-- Initial schema (example)
CREATE SCHEMA IF NOT EXISTS app AUTHORIZATION example_migrations;

-- Grant data rights to example_app on existing tables
GRANT USAGE ON SCHEMA app TO example_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA app TO example_app;

-- Ensure future tables in this schema are also accessible
ALTER DEFAULT PRIVILEGES FOR ROLE example_migrations IN SCHEMA app
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO example_app;