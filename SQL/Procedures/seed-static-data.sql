create or replace procedure seed_static_data()
language plpgsql
as $$
begin
    insert into Role(name) values
   ('admin'),
   ('triator'),
   ('investigator'),
   ('supervisor'),
   ('manager');

    insert into Area(name) values
   ('Car Accident'),
   ('Floods'),
   ('Fire'),
   ('Earthquake');
end;
$$;