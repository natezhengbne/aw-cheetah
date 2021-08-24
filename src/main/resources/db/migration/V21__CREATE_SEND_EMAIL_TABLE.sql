CREATE SCHEMA IF not exists "uatawcheetah";

CREATE TABLE IF NOT EXISTS "email_send" (
        "id" BIGSERIAL NOT NULL PRIMARY KEY,
        "user_id" BIGSERIAL NOT NULL REFERENCES "user_info" (id),
        "receiver" VARCHAR(128) NOT NULL,
        "email_type" VARCHAR(64),
        "send_status" BOOLEAN NOT NULL,
        "send_time" TIMESTAMP WITH TIME ZONE NOT NULL,
        "receive_time" TIMESTAMP WITH TIME ZONE
    );
