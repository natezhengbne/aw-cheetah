DROP TABLE IF EXISTS "USER";
CREATE TYPE status AS ENUM('Unverified', 'Activated', 'Cancelled');
CREATE TABLE "USER" (
    "id" serial PRIMARY KEY,
    "email" VARCHAR(255) UNIQUE NOT NULL,
    "name" VARCHAR(255) NOT NULL,
    "title" VARCHAR(255) NOT NULL,
    "password" CHAR(64) NOT NULL,
    "status" status default 'Unverified',
    "createdTime" TIMESTAMP NOT NULL,
    "updatedTime" TIMESTAMP NOT NULL
);