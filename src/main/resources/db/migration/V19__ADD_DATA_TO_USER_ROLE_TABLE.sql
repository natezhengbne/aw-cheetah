INSERT INTO user_role (user_id, role_id, target_id, is_authorized, created_time, updated_time)
SELECT admin_id, 1, id, true, now(), now()
FROM company
WHERE admin_id IN (SELECT id FROM user_info )
ON conflict do nothing;


INSERT INTO user_role (user_id, role_id, target_id, is_authorized, created_time, updated_time)
SELECT leader_id, 2, id, true, now(), now()
FROM project
WHERE leader_id IN (SELECT id FROM user_info )
ON conflict do nothing;

