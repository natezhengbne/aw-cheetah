CREATE SCHEMA IF not exists "awcheetah";

CREATE TABLE IF NOT EXISTS "todo_board" (

    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "project_id" BIGSERIAL NOT NULL REFERENCES "project" (id),
    "created_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "updated_time" TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS "todo_list" (

    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "todo_board_id" BIGSERIAL NOT NULL REFERENCES "todo_board" (id),
    "company_id" BIGINT NOT NULL,
    "project_id" BIGINT NOT NULL,
    "todo_list_title" VARCHAR(255) NOT NULL,
    "todo_list_details" VARCHAR(255),
    "doc_url" VARCHAR(255) ,
    "created_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "updated_time" TIMESTAMP WITH TIME ZONE NOT NULL
);
