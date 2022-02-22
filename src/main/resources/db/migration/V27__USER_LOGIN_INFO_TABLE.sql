CREATE SCHEMA IF not exists "awcheetah";
DROP TABLE IF EXISTS "user_login_info";
CREATE TABLE "user_login_info"(
  "id" BIGSERIAL PRIMARY KEY,
  "user_id" BIGINT UNIQUE ,
  "login_company_id" BIGINT,
  "created_time" TIMESTAMP WITH TIME ZONE NOT NULL,
  "updated_time" TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX user_idx ON user_login_info(user_id);
