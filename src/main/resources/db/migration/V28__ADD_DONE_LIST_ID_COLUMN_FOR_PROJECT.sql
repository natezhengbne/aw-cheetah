ALTER TABLE "project" ADD "done_list_id" BIGINT;
update "project"
set "done_list_id" = "todo_list".id
    from "todo_list"
where "project".id = "todo_list".project_id AND "todo_list".todo_list_title = 'Done';