ALTER TABLE "todo_item" ADD "item_order" BIGINT;


select todo_item.id,todo_item.todo_list_id,todo_item."item_order"
   from "todo_item","todo_list"
where "todo_item".todo_list_id = "todo_list".id
ORDER BY todo_item.created_time desc;

update "todo_item"
set "item_order" = "todo_item".id


