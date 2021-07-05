CREATE SCHEMA IF not exists "awcheetah";

CREATE TABLE IF NOT EXISTS "message_category" (
    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "project_id" BIGSERIAL NOT NULL REFERENCES "project" (id),
    "category_name" VARCHAR(255) NOT NULL,
    "emoji" VARCHAR(255) NOT NULL
);
ALTER TABLE "message" DROP COLUMN IF exists "category";
ALTER TABLE "message" ADD COLUMN IF not exists "category_id" BIGSERIAL REFERENCES "message_category" (id);
ALTER TABLE "message" ALTER COLUMN "category_id" DROP NOT NULL