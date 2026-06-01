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


    insert into Area(name, boss_id)
    values ('Car Accident', 3),
           ('Floods', null),
           ('Fire', null),
           ('Earthquake', null);

    insert into State(name)
    values ('not_assigned'),
           ('assigned'),
           ('on_going'),
           ('waiting_approval_supervisor'),
           ('approved_by_supervisor'),
           ('rejected_by_supervisor'),
           ('waiting_approval_manager'),
           ('approved_by_manager'),
           ('rejected_by_manager'),
           ('canceled');

end;
$$;