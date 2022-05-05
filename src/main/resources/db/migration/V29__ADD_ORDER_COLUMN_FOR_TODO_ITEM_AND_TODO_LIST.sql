ALTER TABLE "todo_item" ADD "item_order" BIGINT;

UPDATE "todo_item"
SET "item_order" = "todo_item".id;

ALTER TABLE "todo_list" ADD "list_order" BIGINT;

UPDATE "todo_list"
SET "list_order" = "todo_list".id;


