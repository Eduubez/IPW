-- Manual helper to reset the database and insert the current sample data.
-- The Docker init flow uses docker/postgres/init/07-init-data.sql for the same purpose.
call sample_data();
