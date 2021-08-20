CREATE SCHEMA IF NOT EXISTS "awcheetah";

CREATE TABLE IF NOT EXISTS "role" (
    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "name" VARCHAR(255) UNIQUE NOT NULL
);

INSERT INTO "role" ("name") VALUES
('Company Manager'),
('Project Manager');

CREATE TABLE IF NOT EXISTS "user_role" (
    "user_id" BIGSERIAL NOT NULL REFERENCES "user_info"(id),
    "role_id" BIGSERIAL NOT NULL REFERENCES "role"(id),
    "target_id" BIGSERIAL NOT NULL,
    "is_authorized" BOOLEAN NOT NULL,
    "created_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "updated_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (user_id, role_id, target_id)
);