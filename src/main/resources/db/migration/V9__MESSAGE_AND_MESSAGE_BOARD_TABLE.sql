CREATE SCHEMA IF not exists "awcheetah";



CREATE TABLE IF NOT EXISTS "message" (
    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "company_id" BIGSERIAL NOT NULL,
    "project_id" BIGSERIAL NOT NULL REFERENCES "project" (id),
    "poster_user_id" BIGSERIAL,
    "category" VARCHAR(255),
    "message_title" VARCHAR(255) NOT NULL,
    "doc_url" VARCHAR(512),
    "content" VARCHAR(10000),
    "post_time" TIMESTAMP WITH TIME ZONE,
    "created_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "updated_time" TIMESTAMP WITH TIME ZONE NOT NULL
    );