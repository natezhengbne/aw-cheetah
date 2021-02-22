CREATE SCHEMA if not exists "awcheetah";

DROP TABLE IF EXISTS "user";
CREATE TABLE "user" (
    "id" BIGINT PRIMARY KEY,
    "email" VARCHAR(255) UNIQUE NOT NULL,
    "name" VARCHAR(255) NOT NULL,
    "title" VARCHAR(255),
    "password" CHAR(64) NOT NULL,
    "status" VARCHAR(255) default 'UNVERIFIED',
    "created_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "updated_time" TIMESTAMP WITH TIME ZONE NOT NULL
);