ALTER TABLE "todo_item" ADD "pending_id"  BIGINT;
UPDATE "todo_item"
SET "pending_id" = "todo_list".id
    FROM "todo_list"
WHERE "todo_item".todo_list_id ="todo_list".id