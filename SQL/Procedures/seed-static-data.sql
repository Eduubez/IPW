create or replace procedure seed_static_data()
    language plpgsql
as
$$
begin
    insert into Role(name)
    values ('admin'),
           ('triator'),
           ('investigator'),
           ('supervisor'),
           ('manager');

    insert into Users(name, email, password_hash, area_id)
    values ('Root User', 'root@example.com', '$2b$10$placeholderhashroot', null);

    insert into Area(name,boss_id)
    values ('Car Accident', 1),
           ('Floods', 1),
           ('Fire', 1),
           ('Earthquake', 1);
end;
$$;

call seed_static_data();
call sample_data();
select *
from area;

update area set boss_id = 8 where name = 'Car Accident';
