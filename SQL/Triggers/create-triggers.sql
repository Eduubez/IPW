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
execute function close_previous_state()