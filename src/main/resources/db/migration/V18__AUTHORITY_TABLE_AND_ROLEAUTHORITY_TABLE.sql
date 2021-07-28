CREATE SCHEMA IF NOT EXISTS "awcheetah";

CREATE TABLE IF NOT EXISTS "authority" (
    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "name" VARCHAR(255) UNIQUE NOT NULL
);

INSERT INTO "authority" ("name") VALUES
('add employee'),
('edit company description'),
('add project'),
('add project member'),
('edit project description'),
('edit to-do'),
('edit message');

CREATE TABLE IF NOT EXISTS "role_authority" (
    "role_id" BIGSERIAL NOT NULL REFERENCES "role"(id),
    "authority_id" BIGSERIAL NOT NULL REFERENCES "authority"(id),
    UNIQUE (role_id, authority_id)
);

INSERT INTO "role_authority" VALUES
(1,1),
(1,2),
(1,3),
(1,4),
(1,5),
(1,6),
(1,7),
(2,4),
(2,5),
(2,6),
(2,7),
(3,4),
(3,6),
(3,7);

