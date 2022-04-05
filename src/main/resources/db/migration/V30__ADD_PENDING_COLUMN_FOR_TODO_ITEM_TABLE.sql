ALTER TABLE "todo_item" ADD "pending_id"  BIGINT;
update "todo_item"
set "pending_id" = "todo_list".id
    from "todo_list"
where "todo_item".todo_list_id ="todo_list".id