CREATE SCHEMA IF NOT EXISTS "awcheetah";

CREATE TABLE IF NOT EXISTS "authority" (
    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "name" VARCHAR(255) UNIQUE NOT NULL
);

INSERT INTO "authority" ("name") VALUES
('add employee'),
('edit company description'),
('edit project description');

CREATE TABLE IF NOT EXISTS "role_authority" (
    "role_id" BIGSERIAL NOT NULL REFERENCES "role"(id),
    "authority_id" BIGSERIAL NOT NULL REFERENCES "authority"(id),
    UNIQUE (role_id, authority_id)
);

INSERT INTO "role_authority" VALUES
(1,1),
(1,2),
(1,3),
(2,3);


