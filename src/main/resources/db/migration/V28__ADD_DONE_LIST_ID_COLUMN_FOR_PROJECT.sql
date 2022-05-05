ALTER TABLE "project" ADD "done_list_id" BIGINT;
UPDATE "project"
SET "done_list_id" = "todo_list".id
    FROM "todo_list"
WHERE "project".id = "todo_list".project_id AND "todo_list".todo_list_title = 'Done';