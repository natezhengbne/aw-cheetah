CREATE SCHEMA IF NOT EXISTS "awcheetah";

CREATE TABLE IF NOT EXISTS "role" (
    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "name" VARCHAR(255) UNIQUE NOT NULL
);

INSERT INTO "role" ("name") VALUES
('Company Owner'),
('Project Owner'),
('Project Member');

CREATE TABLE IF NOT EXISTS "user_role" (
    "user_id" BIGSERIAL NOT NULL REFERENCES "user_info"(id),
    "role_id" BIGSERIAL NOT NULL REFERENCES "role"(id),
    UNIQUE (user_id, role_id)
);