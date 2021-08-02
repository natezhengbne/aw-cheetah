CREATE SCHEMA IF not exists "uatawcheetah";

CREATE TABLE IF NOT EXISTS "project" (

    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "leader_id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "is_private" BOOLEAN NOT NULL,
    "is_deleted" BOOLEAN NOT NULL,
    "description" VARCHAR(255),
    "start_date" date,
    "end_date" date,
    "created_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "updated_time" TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS "project_user" (
    "user_id" BIGSERIAL NOT NULL REFERENCES "user_info" (id),
    "project_id" BIGSERIAL NOT NULL REFERENCES "project" (id),
    "attended" BOOLEAN NOT NULL,
    "created_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "updated_time" TIMESTAMP WITH TIME ZONE NOT NULL,
                                 UNIQUE (user_id, project_id)
);