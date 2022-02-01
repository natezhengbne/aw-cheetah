CREATE SCHEMA if not exists "awcheetah";
DROP TABLE IF EXISTS "event";
CREATE TABLE "event" (
    "id" BIGSERIAL PRIMARY KEY,
    "owner_id" BIGINT NOT NULL REFERENCES "user_info" (id),
    "company_id" BIGINT NOT NULL REFERENCES "company" (id),
    "project_id" BIGINT NOT NULL REFERENCES "project" (id),
    "title" VARCHAR(128) NOT NULL,
    "description" VARCHAR(2048),
    "all_day_event" BOOLEAN NOT NULL,
    "start_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "end_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "created_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "updated_time" TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX event_company_project_owner_idx ON event (company_id, project_id, owner_id);