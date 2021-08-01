CREATE SCHEMA IF not exists "uatawcheetah";

CREATE TABLE IF NOT EXISTS "todo_item" (

    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "todo_list_id" BIGSERIAL NOT NULL REFERENCES "todo_list" (id),
    "company_id" BIGINT NOT NULL,
    "project_id" BIGINT NOT NULL,
    "event_doc_url" VARCHAR(255),
    "content" VARCHAR(255),
    "doc_url" VARCHAR(255),
    "description" VARCHAR(255),
    "completed" BOOLEAN NOT NULL,
    "due_date" DATE,
    "created_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "updated_time" TIMESTAMP WITH TIME ZONE NOT NULL
);
