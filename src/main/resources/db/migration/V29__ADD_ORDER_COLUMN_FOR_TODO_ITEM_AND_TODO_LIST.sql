ALTER TABLE "todo_item" ADD "item_order" BIGINT;

update "todo_item"
set "item_order" = "todo_item".id;

ALTER TABLE "todo_list" ADD "list_order" BIGINT;

update "todo_list"
set "list_order" = "todo_list".id;


