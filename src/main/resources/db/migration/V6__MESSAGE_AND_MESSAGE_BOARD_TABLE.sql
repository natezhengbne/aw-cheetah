CREATE SCHEMA IF not exists "awcheetah";

CREATE TABLE IF NOT EXISTS "message_board" (
    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "project_id" BIGSERIAL NOT NULL REFERENCES "project" (id),
    "created_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "updated_time" TIMESTAMP WITH TIME ZONE NOT NULL
    );

CREATE TABLE IF NOT EXISTS "message" (
    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "message_board_id" BIGSERIAL NOT NULL REFERENCES "message_board" (id),
    "company_id" BIGSERIAL NOT NULL,
    "project_id" BIGSERIAL NOT NULL,
    "poster_user_id" BIGSERIAL,
    "category" VARCHAR(255),
    "message_title" VARCHAR(255) NOT NULL,
    "doc_url" VARCHAR(255) NOT NULL,
    "content" VARCHAR(10000),
    "post_time" TIMESTAMP WITH TIME ZONE,
    "created_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "updated_time" TIMESTAMP WITH TIME ZONE NOT NULL
    );