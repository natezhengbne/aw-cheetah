CREATE SCHEMA IF not exists "awcheetah";

CREATE TABLE IF NOT EXISTS "todo_item" (

    "todo_item_id" BIGSERIAL NOT NULL PRIMARY KEY,
    "company_id" BIGSERIAL NOT NULL REFERENCES "company" (id),
    "project_id" BIGSERIAL NOT NULL REFERENCES "project" (id),
    "todo_list_id" BIGSERIAL NOT NULL REFERENCES "todo_list" (id),
    "event_doc_url" VARCHAR(255),
    "content" VARCHAR(255),
    "doc_url" VARCHAR(255),
    "description" VARCHAR(255),
    "completed" BOOLEAN NOT NULL,
    "due_date" DATE,
    "created_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "updated_time" TIMESTAMP WITH TIME ZONE NOT NULL
);
