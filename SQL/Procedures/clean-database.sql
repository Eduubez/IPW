create or replace procedure clean_database()
language plpgsql
as $$
begin
    truncate table
        Role_Permission,
        User_Role,
        Token,
        Activity,
        Notes,
        State,
        Report,
        Proves,
        Diligence,
        Process,
        Permission,
        Role,
        Users,
        Typification,
        Insurance,
        Location,
        Area
        restart identity cascade;
end;
$$;