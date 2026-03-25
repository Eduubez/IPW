create or replace procedure clean_database()
language plpgsql
as $$
begin
    truncate table
        Role_Permission,
        User_Role,
        Token,
        State,
        Report,
        Proves,
        Diligencia,
        Process,
        Permission,
        Role,
        Users,
        Typification,
        Area
        restart identity cascade;
end;
$$;