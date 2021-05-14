DROP TABLE IF EXISTS "todo_list";
DROP TABLE IF EXISTS "todo_board";

CREATE TABLE IF NOT EXISTS "todo_list" (

    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "project_id" BIGINT NOT NULL REFERENCES "project" (id),
    "company_id" BIGINT NOT NULL,
    "todo_list_title" VARCHAR(255) NOT NULL,
    "todo_list_details" VARCHAR(2048),
    "doc_url" VARCHAR(255) ,
    "created_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "updated_time" TIMESTAMP WITH TIME ZONE NOT NULL
);
