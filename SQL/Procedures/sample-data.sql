create or replace procedure sample_data()
    language plpgsql
as $$
declare
    v_triator_id        int;
    v_investigator_id   int;
    v_supervisor_id     int;
    v_manager_id        int;
    v_admin_id          int;
    v_insurance_id      int;
    v_location_id       int;
    v_area_car          int;
    v_area_fire         int;
    v_type_id           int;
begin
    call clean_database();
    call seed_static_data();

    -- Insert Typifications
    insert into Typification(name, honorary) values ('Collision', 500.00) returning id into v_type_id;
    insert into Typification(name, honorary) values ('Total Loss', 1200.00);
    insert into Typification(name, honorary) values ('Fire Damage', 900.00);

    -- Insert Insurances
    insert into Insurance(name) values ('Allianz') returning id into v_insurance_id;
    insert into Insurance(name) values ('Fidelidade');
    insert into Insurance(name) values ('Mapfre');

    -- Get area ids
    select id into v_area_car from Area where name = 'Car Accident';
    select id into v_area_fire from Area where name = 'Fire';

    -- Insert 5 users. All sample passwords are '12345'.
    insert into Users(name, email, password_hash, area_id) values
        ('Tiago Triador',     'tiago@ipw.pt',      '$2a$10$E8Rg/TrDsbjQnr2QY/cbreSuzOl0iQizuGzDoSs6h24StXCHgza7a', null)
    returning id into v_triator_id;

    insert into Users(name, email, password_hash, area_id) values
        ('Ana Averiguador',  'ana@ipw.pt',         '$2a$10$38tAgpNUr2KptI3NGjsbFO0L0Hhnyaut6iQy7lrogidVIFS9QJeBq', v_area_car)
    returning id into v_investigator_id;

    insert into Users(name, email, password_hash, area_id) values
        ('Sofia Supervisor',  'sofia@ipw.pt',       '$2a$10$0hssu/FOYwHfbWNMNh9a3uMVaqrX2/Wdotv6tpcvi2ARAhgWAuyZG', v_area_car)
    returning id into v_supervisor_id;

    insert into Users(name, email, password_hash, area_id) values
        ('Gabriel Gestor',     'gabriel@ipw.pt',       '$2a$10$sDiI5XyQcOvvAbdCiA0cKu6OFJFjA.vP1sEaPHAJCw1AiBwyGakpS', null)
    returning id into v_manager_id;

    -- 5th user has NO processes at all
    insert into Users(name, email, password_hash, area_id) values
        ('Andreia Administrador',     'andreia@ipw.pt',         '$2a$10$OOJLQvwls4eECyUz11clh.dXkwQrNAB6ILaEV.s5n68dpeMQ4D1Pi', null)
    returning id into v_admin_id;

    -- Assign roles
    insert into User_Role(user_id, role_name) values
                                                  (v_triator_id,      'triator'),
                                                  (v_investigator_id, 'investigator'),
                                                  (v_supervisor_id,   'supervisor'),
                                                  (v_manager_id,      'manager'),
                                                  (v_admin_id,        'admin');

    -- -----------------------------------------------------------------------
    -- 10 Processes
    -- Tiago is triator on all 10 processes.
    -- Andreia has no process assigned.
    -- Processes 1-5 are fully assigned, so the trigger sets 'assigned'.
    -- Processes 6-10 only have a triator, so the trigger sets 'not_assigned'.
    -- -----------------------------------------------------------------------

    -- Insert a shared location
    insert into Location(district, county, street, latitude, longitude)
    values ('Lisbon', 'Lisbon', 'Rua Augusta 1', 38.7071, -9.1368)
    returning id into v_location_id;

    -- Process 1
    insert into Process(name, insurance_id, location, creation_date, due_date, is_suspect_fraud, priority, area_id, typification_id, triator_id, investigator_id, supervisor_id)
    values ('Process Alpha',   v_insurance_id, v_location_id, now(), now() + interval '30 days', false, 'normal',        v_area_car,  v_type_id, v_triator_id, v_investigator_id, v_supervisor_id);

    -- Process 2
    insert into Process(name, insurance_id, location, creation_date, due_date, is_suspect_fraud, priority, area_id, typification_id, triator_id, investigator_id, supervisor_id)
    values ('Process Beta',    v_insurance_id, v_location_id, now(), now() + interval '20 days', false, 'with_priority', v_area_car,  v_type_id, v_triator_id, v_investigator_id, v_supervisor_id);

    -- Process 3
    insert into Process(name, insurance_id, location, creation_date, due_date, is_suspect_fraud, priority, area_id, typification_id, triator_id, investigator_id, supervisor_id)
    values ('Process Gamma',   v_insurance_id, v_location_id, now(), now() + interval '15 days', true,  'urgent',        v_area_car,  v_type_id, v_triator_id, v_investigator_id, v_supervisor_id);

    -- Process 4
    insert into Process(name, insurance_id, location, creation_date, due_date, is_suspect_fraud, priority, area_id, typification_id, triator_id, investigator_id, supervisor_id)
    values ('Process Delta',   v_insurance_id, v_location_id, now(), now() + interval '45 days', false, 'normal',        v_area_car,  v_type_id, v_triator_id, v_investigator_id, v_supervisor_id);

    -- Process 5
    insert into Process(name, insurance_id, location, creation_date, due_date, is_suspect_fraud, priority, area_id, typification_id, triator_id, investigator_id, supervisor_id)
    values ('Process Epsilon', v_insurance_id, v_location_id, now(), now() + interval '10 days', true,  'urgent',        v_area_car,  v_type_id, v_triator_id, v_investigator_id, v_supervisor_id);

    -- Process 6 (not assigned — no investigator/supervisor)
    insert into Process(name, insurance_id, location, creation_date, due_date, is_suspect_fraud, priority, area_id, typification_id, triator_id, investigator_id, supervisor_id)
    values ('Process Zeta',    v_insurance_id, v_location_id, now(), now() + interval '60 days', false, 'normal',        v_area_car,  v_type_id, v_triator_id, null, null);

    -- Process 7
    insert into Process(name, insurance_id, location, creation_date, due_date, is_suspect_fraud, priority, area_id, typification_id, triator_id, investigator_id, supervisor_id)
    values ('Process Eta',     v_insurance_id, v_location_id, now(), now() + interval '60 days', false, 'with_priority', v_area_car,  v_type_id, v_triator_id, null, null);

    -- Process 8
    insert into Process(name, insurance_id, location, creation_date, due_date, is_suspect_fraud, priority, area_id, typification_id, triator_id, investigator_id, supervisor_id)
    values ('Process Theta',   v_insurance_id, v_location_id, now(), now() + interval '60 days', true,  'urgent',        v_area_fire, v_type_id, v_triator_id, null, null);

    -- Process 9
    insert into Process(name, insurance_id, location, creation_date, due_date, is_suspect_fraud, priority, area_id, typification_id, triator_id, investigator_id, supervisor_id)
    values ('Process Iota',    v_insurance_id, v_location_id, now(), now() + interval '60 days', false, 'normal',        v_area_fire, v_type_id, v_triator_id, null, null);

    -- Process 10
    insert into Process(name, insurance_id, location, creation_date, due_date, is_suspect_fraud, priority, area_id, typification_id, triator_id, investigator_id, supervisor_id)
    values ('Process Kappa',   v_insurance_id, v_location_id, now(), now() + interval '60 days', false, 'normal',        v_area_car,  v_type_id, v_triator_id, null, null);

end;
$$;

