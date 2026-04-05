/*
Fecha automaticamente o estado anterior de um processo ao inserir um novo estado.
Garante que apenas existe um estado ativo (end_date = null) por processo.
*/

create or replace function close_previous_state()
returns trigger as $$
begin
    update State
    set end_date = NEW.start_date
    where process_id = NEW.process_id
        and end_date is null;

    return NEW;
end;
$$ language plpgsql;

create trigger trigger_close_previous_state
before insert on State
for each row
execute function close_previous_state();

/*
Atualiza automaticamente o campo updated_at quando um Report é modificado.
Evita que o backend tenha de gerir manualmente esta informação.
*/

create or replace function set_report_updated_at()
returns trigger as $$
begin
    new.updated_at = current_timestamp;
    return new;
end;
$$ language plpgsql;

create trigger trigger_set_report_updated_at
before update on Report
for each row
execute function set_report_updated_at();

/*
Cria automaticamente o estado inicial de um processo após inserção.
Define 'assigned' se já tiver investigator e supervisor, caso contrário 'not_assigned'.
*/

create or replace function create_initial_state_for_process()
returns trigger as $$
begin
    if new.investigator_id is not null and new.supervisor_id is not null then
        insert into State(process_id, name, start_date)
        values (new.id, 'assigned', current_timestamp);
    else
        insert into State(process_id, name, start_date)
        values (new.id, 'not_assigned', current_timestamp);
    end if;

    return new;
end;
$$ language plpgsql;

create trigger trigger_create_initial_state_for_process
after insert on Process
for each row
execute function create_initial_state_for_process();

/*
Deteta quando um processo passa a ter investigator e supervisor definidos.
Insere automaticamente o estado 'assigned', fechando o estado anterior.
*/

create or replace function set_process_assigned_state_when_fully_assigned()
returns trigger as $$
begin
    if
        (
            old.investigator_id is null
            or old.supervisor_id is null
        )
        and
        (
            new.investigator_id is not null
            and new.supervisor_id is not null
        )
    then
        insert into State(process_id, name, start_date)
        values (new.id, 'assigned', current_timestamp);
    end if;

    return new;
end;
$$ language plpgsql;

create trigger trigger_set_process_assigned_state_when_fully_assigned
    after update on Process
    for each row
execute function set_process_assigned_state_when_fully_assigned();