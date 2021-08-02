insert into awcheetah.user_role (user_id, role_id)
select admin_id,1
from awcheetah.company
where admin_id in (select id from awcheetah.user_info )
on conflict do nothing


insert into awcheetah.user_role (user_id, role_id)
select leader_id,2
from awcheetah.project
where leader_id in (select id from awcheetah.user_info )
on conflict do nothing


insert into awcheetah.user_role (user_id, role_id)
select user_id,3
from awcheetah.project_user
where user_id in (select id from awcheetah.user_info )
on conflict do nothing
