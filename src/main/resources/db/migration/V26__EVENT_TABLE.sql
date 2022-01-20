CREATE SCHEMA if not exists "awcheetah";
DROP TABLE IF EXISTS "event";
CREATE TABLE "event" (
    "id" BIGSERIAL PRIMARY KEY,
    "owner_id" BIGINT NOT NULL REFERENCES "user_info" (id),
    "title" VARCHAR(128) UNIQUE NOT NULL,
    "description" VARCHAR(2048) NOT NULL,
    "all_day_event" BOOLEAN NOT NULL,
    "start_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "end_time" TIMESTAMP WITH TIME ZONE NOT NULL
);
