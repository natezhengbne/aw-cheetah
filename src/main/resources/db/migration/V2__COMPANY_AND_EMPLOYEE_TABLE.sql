CREATE SCHEMA IF not exists "awcheetah";

CREATE TABLE IF NOT EXISTS "company" (

    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "description" VARCHAR(255),
    "website" VARCHAR(255),
    "admin_id" BIGINT NOT NULL,
    "contact_number" VARCHAR(255),
    "contact_email" VARCHAR(255),
    "industry" VARCHAR(255),
    "created_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "updated_time" TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS "employee" (
    "user_id" BIGSERIAL NOT NULL REFERENCES "user" (id),
    "company_id" BIGSERIAL NOT NULL REFERENCES "company" (id),
    "title" VARCHAR(255),
    "created_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (user_id, company_id)
);


