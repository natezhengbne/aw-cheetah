ALTER TABLE "todo_list" ADD "is_done_list" BOOLEAN;
UPDATE "todo_list" SET is_done_list = true where todo_list_title = 'Done'