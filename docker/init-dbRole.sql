-- Runs only on first init of the Postgres data directory in docker-compose setup

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_roles WHERE rolname = 'example_app'
    ) THEN
        CREATE ROLE example_app
            LOGIN
            PASSWORD 'IAmAV3ryStr0ngPa55word';
    END IF;
END
$$;