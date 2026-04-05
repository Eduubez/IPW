
create table Area(
                     id   serial primary key,
                     name varchar(100) not null unique
);

create table Users(
    id            serial primary key,
    name          varchar(150) not null,
    email         varchar(150) not null unique,
    password_hash varchar(255) not null,
    area_id       int null references Area(id),
    is_active     boolean not null default true
);


create table Token(
    token       varchar(255) primary key,
    user_id     int not null references Users(id) on delete cascade,
    created_at  timestamp not null default current_timestamp,
    expires_at  timestamp not null
);

create table Role(
    name varchar(255) not null primary key
        check (name in
           (
           'admin',
           'triator',
           'investigator',
           'supervisor',
           'manager'
           )
        )
);

create table User_Role(
    user_id int not null references Users(id),
    role_name varchar(50) not null references Role(name),
    primary key (user_id, role_name)
);

create table Permission(
    name varchar(100) primary key
);

create table Role_Permission(
    role_name       varchar(50) not null references Role(name),
    permission_name varchar(100) not null references Permission(name),
    primary key (role_name, permission_name)
);

create table Typification(
    id       serial primary key,
    name     varchar(100) not null unique,
    honorary numeric(10,2)
);

create table Insurance(
    id      serial primary key,
    name    varchar(150) not null unique
);

create table Process(
    id                  serial primary key,
    name                varchar(150) not null,
    insurance_id        int not null references Insurance(id),
    location            varchar(255) not null,
    creation_date       timestamp not null default current_timestamp,
    due_date            timestamp,
    priority            varchar(20) not null
        check (priority in ('normal', 'with_priority', 'urgent')),

    area_id             int not null references Area(id),
    typification_id     int not null references Typification(id),

    triator_id          int not null references Users(id),
    investigator_id     int references Users(id),
    supervisor_id       int references Users(id),
    constraint due_date_higher_than_creation_date
        check(due_date is null or due_date >= creation_date)
);

create table Diligence(
    id              serial primary key,
    process_id      int not null references Process(id),
    investigator_id int not null references Users(id),
    description     text not null,
    status          varchar(20) not null
       check (status in ('pending', 'on_going', 'completed', 'canceled')),
    created_at      timestamp not null default current_timestamp,
    due_date        timestamp,
    completed_at    timestamp
);

create table State(
    id          serial primary key,
    process_id  int not null references Process(id),
    name        varchar(100) not null
              check( name in
                  (
                  'not_assigned',
                  'assigned',
                  'on_going',
                  'waiting_approval_supervisor',
                  'approved_by_supervisor',
                  'rejected_by_supervisor',
                  'waiting_approval_manager',
                  'approved_by_manager',
                  'rejected_by_manager',
                  'canceled'
                  )
              ),
    start_date  timestamp not null default current_timestamp,
    end_date    timestamp
);

create table Report(
    process_id  int primary key references Process(id),
    content     text not null,
    created_at  timestamp not null default current_timestamp,
    updated_at  timestamp not null default current_timestamp
);

create table Proves(
    id          serial primary key,
    process_id  int not null references Process(id),
    file_name   varchar(255) not null,
    file_type   varchar(100) not null,
    file_url    varchar(500) not null,
    created_at  timestamp not null default current_timestamp
);

create table Notes(
    id          serial primary key,
    process_id  int references Process(id),
    proves_id   int references Proves(id),
    content     text not null,
    author_id   int not null references Users(id),
    created_at timestamp not null default current_timestamp,
    constraint notes_process_or_proves_check
        check (
            (process_id is not null  and proves_id is null) or
            (process_id is null and proves_id is not null)
        )
);

create table Activity(
    id          serial primary key,
    process_id  int not null references Process(id),
    user_id     int not null references Users(id),
    action      varchar(100) not null,
    description text,
    created_at  timestamp not null default current_timestamp
);
