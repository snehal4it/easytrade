CREATE ROLE localdba WITH LOGIN SUPERUSER CREATEDB CREATEROLE;
CREATE DATABASE easytrade OWNER localdba;

CREATE ROLE easytrade_app WITH LOGIN NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;