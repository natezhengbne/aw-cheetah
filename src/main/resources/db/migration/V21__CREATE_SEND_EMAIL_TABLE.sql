CREATE SCHEMA IF not exists "uatawcheetah";

CREATE TABLE IF NOT EXISTS "email_send" (
    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "user_id" BIGSERIAL NOT NULL REFERENCES "user_info" (id),
    "is_sent" BOOLEAN NOT NULL,
    "update_time" TIMESTAMP WITH TIME ZONE NOT NULL
);
