#!/bin/sh

# Execute the following command to allow the script to be executed on MacOS:
#   xattr -d com.apple.quarantine recreate_demo_postgresql_database_macos.sh

dropdb --if-exists demo
createdb  --template=template0 --encoding=UTF8 demo
psql --dbname=demo --command="DROP ROLE IF EXISTS demo;"
psql --dbname=demo --command="CREATE ROLE demo WITH LOGIN PASSWORD 'demo';"
psql --dbname=demo --command="GRANT ALL PRIVILEGES ON DATABASE demo to demo;"
psql --dbname=demo --command="ALTER USER demo WITH SUPERUSER;"
psql --dbname=demo --command="CREATE SCHEMA liquibase;"



