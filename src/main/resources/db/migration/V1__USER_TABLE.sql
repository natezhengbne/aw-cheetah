CREATE SCHEMA if not exists "awcheetah";
DROP TABLE IF EXISTS "user";
CREATE TYPE status AS ENUM('Unverified', 'Activated', 'Cancelled');
CREATE TABLE "user" (
    "id" BIGINT PRIMARY KEY,
    "email" VARCHAR(255) UNIQUE NOT NULL,
    "name" VARCHAR(255) NOT NULL,
    "title" VARCHAR(255) NOT NULL,
    "password" CHAR(64) NOT NULL,
    "status" status default 'Unverified',
    "createdTime" TIMESTAMP WITH TIME ZONE NOT NULL,
    "updatedTime" TIMESTAMP WITH TIME ZONE NOT NULL
);