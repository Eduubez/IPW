create or replace procedure reset_database()
language plpgsql
as $$
begin
    call clean_database();
    call seed_static_data();
end;
$$;