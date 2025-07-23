Demo for my 5+ - SQL pagination methods with pros and cons for each

Start Postgres Database before running (in a terminal):
 - brew services start postgresql
 - psql postgres
 - CREATE DATABASE paginationdb;
 - CREATE USER myuser WITH ENCRYPTED PASSWORD 'mypassword';
 - GRANT ALL PRIVILEGES ON DATABASE paginationdb TO myuser;
 - ALTER DEFAULT PRIVILEGES FOR ROLE john_kallen IN SCHEMA public GRANT SELECT, UPDATE, INSERT, DELETE ON TABLES TO myuser;

Connect to database
 - psql -U myuser -d paginationdb
 -  postgres=# \dt *.* -- List all schemas and tables
 -  postgres=# \dt public.* -- List public schema and all tables
 - SELECT * FROM pg_settings WHERE name = 'port'; -- Get Port
 - SHOW SEARCH_PATH; --Show path (default schema)
 - SET SEARCH_PATH = public;
 - \dn -- List of Schemas
 - \l -- list databases
 - \c paginationdb -- change to database
 - \dt -- show database tables


Use SQL in resources/schema.sql to create data

