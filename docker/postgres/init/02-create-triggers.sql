/*
Fecha automaticamente o estado anterior de um processo ao inserir um novo Process_State.
Garante que apenas existe um estado ativo (end_date = null) por processo.
Dispara em Process_State (AFTER INSERT) pois é aí que está a relação processo↔estado.
*/

create or replace function close_previous_state()
    returns trigger as $$
begin
    update State s
    set end_date = (select start_date from State where id = NEW.state_id)
    from Process_State ps
    where ps.process_id = NEW.process_id
      and ps.state_id   = s.id
      and s.end_date    is null
      and s.id          != NEW.state_id;

    return NEW;
end;
$$ language plpgsql;

create trigger trigger_close_previous_state
    after insert on Process_State
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
Insere em State e depois em Process_State para estabelecer a relação.
*/

create or replace function create_initial_state_for_process()
    returns trigger as $$
declare
    new_state_id int;
    initial_state varchar(100);
begin
    if new.investigator_id is not null and new.supervisor_id is not null then
        initial_state := 'assigned';
    else
        initial_state := 'not_assigned';
    end if;

    insert into State(name, start_date)
    values (initial_state, current_timestamp)
    returning id into new_state_id;

    insert into Process_State(process_id, state_id)
    values (new.id, new_state_id);

    return new;
end;
$$ language plpgsql;

create trigger trigger_create_initial_state_for_process
    after insert on Process
    for each row
execute function create_initial_state_for_process();

/*
Deteta quando um processo passa a ter investigator e supervisor definidos.
Insere automaticamente o estado 'assigned' em State e liga via Process_State,
fechando o estado anterior (tratado pelo trigger_close_previous_state).
*/

create or replace function set_process_assigned_state_when_fully_assigned()
    returns trigger as $$
declare
    new_state_id int;
begin
    if  (old.investigator_id is null or  old.supervisor_id is null)
        and (new.investigator_id is not null and new.supervisor_id is not null)
    then
        insert into State(name, start_date)
        values ('assigned', current_timestamp)
        returning id into new_state_id;

        insert into Process_State(process_id, state_id)
        values (new.id, new_state_id);
    end if;

    return new;
end;
$$ language plpgsql;

create trigger trigger_set_process_assigned_state_when_fully_assigned
    after update on Process
    for each row
execute function set_process_assigned_state_when_fully_assigned();
