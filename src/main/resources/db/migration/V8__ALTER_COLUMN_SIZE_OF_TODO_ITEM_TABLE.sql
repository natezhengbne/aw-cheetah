ALTER TABLE "todo_item" ALTER COLUMN "description" TYPE VARCHAR(512);
ALTER TABLE "todo_item" ALTER COLUMN "doc_url" TYPE VARCHAR(512);
ALTER TABLE "todo_item" ALTER COLUMN "event_doc_url" TYPE VARCHAR(512);
ALTER TABLE "todo_item" RENAME "content" TO "notes";
ALTER TABLE "todo_item" ALTER COLUMN "notes" TYPE VARCHAR(10000);
