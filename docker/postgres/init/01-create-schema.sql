drop table if exists Activity cascade;
drop table if exists Notes cascade;
drop table if exists Proves cascade;
drop table if exists Report cascade;
drop table if exists State cascade;
drop table if exists Diligence cascade;
drop table if exists Process cascade;
drop table if exists Location cascade;
drop table if exists Insurance cascade;
drop table if exists Typification cascade;
drop table if exists Role_Permission cascade;
drop table if exists Permission cascade;
drop table if exists User_Role cascade;
drop table if exists AccessToken cascade;
drop table if exists LoginToken cascade;
drop table if exists RefreshToken cascade;
drop table if exists Role cascade;
drop table if exists Users cascade;
drop table if exists Area cascade;
drop table if exists Process_State cascade;


create table Area
(
    id      serial primary key,
    name    varchar(100) not null unique,
    boss_id int
);

create table Users
(
    id            serial primary key,
    name          varchar(150) not null,
    email         varchar(150) not null unique,
    password_hash varchar(255) not null,
    area_id       int          null references Area (id),
    is_active     boolean      not null default true
);

create table Role
(
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

create table AccessToken
(
    token      text primary key,
    user_id    int         not null references Users (id) on delete cascade,
    role       varchar(50) not null references Role (name),
    created_at timestamp   not null default current_timestamp,
    expires_at timestamp   not null
);

create table LoginToken
(
    token      text primary key,
    user_id    int       not null references Users (id) on delete cascade,
    created_at timestamp not null default current_timestamp,
    expires_at timestamp not null
);

create table RefreshToken
(
    token      text primary key,
    user_id    int         not null references Users (id) on delete cascade,
    role       varchar(50) not null references Role (name),
    created_at timestamp   not null default current_timestamp,
    expires_at timestamp   not null
);

create table User_Role
(
    user_id   int         not null references Users (id),
    role_name varchar(50) not null references Role (name),
    primary key (user_id, role_name)
);

create table Permission
(
    name varchar(100) primary key
);

create table Role_Permission
(
    role_name       varchar(50)  not null references Role (name),
    permission_name varchar(100) not null references Permission (name),
    primary key (role_name, permission_name)
);

create table Typification
(
    id       serial primary key,
    name     varchar(100) not null unique,
    honorary numeric(10, 2)
);

create table Insurance
(
    id   serial primary key,
    name varchar(150) not null unique
);

create table Location
(
    id        serial primary key,
    district  varchar(100),
    county    varchar(100),
    street    varchar(255),
    latitude  numeric(10, 7),
    longitude numeric(10, 7)
);

create table Process
(
    id               serial primary key,
    name             varchar(150) not null,
    insurance_id     int          not null references Insurance (id),
    location         int          not null references Location (id),
    creation_date    timestamp    not null default current_timestamp,
    due_date         timestamp,
    is_suspect_fraud boolean      not null default false,
    priority         varchar(20)  not null
        check (priority in ('normal', 'with_priority', 'urgent')),

    area_id          int          not null references Area (id),
    typification_id  int          not null references Typification (id),

    triator_id       int          not null references Users (id),
    investigator_id  int references Users (id),
    supervisor_id    int references Users (id),
    constraint due_date_higher_than_creation_date
        check (due_date is null or due_date >= creation_date)
);

create table Diligence
(
    id              serial primary key,
    process_id      int         not null references Process (id),
    investigator_id int         not null references Users (id),
    description     text        not null,
    status          varchar(20) not null
        check (status in ('pending', 'on_going', 'completed', 'canceled')),
    created_at      timestamp   not null default current_timestamp,
    due_date        timestamp,
    completed_at    timestamp
);

create table State
(
    id         serial primary key,
    name       varchar(100) primary key
        check ( name in
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
            )

);

create table Process_State
(
    process_id int not null references Process (id),
    state_id   int not null references State (id),
    start_date timestamp    not null default current_timestamp,
    end_date   timestamp
        constraint state_end_date_after_start_date
            check (end_date is null or end_date >= start_date)
);

create table Report
(
    id         serial primary key,
    process_id int       not null references Process (id),
    content    text      not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

create table Proves
(
    id         serial primary key,
    process_id int          not null references Process (id),
    file_name  varchar(255) not null,
    file_type  varchar(100) not null,
    file_url   varchar(500) not null,
    created_at timestamp    not null default current_timestamp
);

create table Notes
(
    id         serial primary key,
    process_id int references Process (id),
    proves_id  int references Proves (id),
    content    text      not null,
    author_id  int       not null references Users (id),
    created_at timestamp not null default current_timestamp,
    constraint notes_process_or_proves_check
        check (
            (process_id is not null and proves_id is null) or
            (process_id is null and proves_id is not null)
            )
);

create table Activity
(
    id          serial primary key,
    process_id  int          not null references Process (id),
    user_id     int          not null references Users (id),
    action      varchar(100) not null,
    description text,
    created_at  timestamp    not null default current_timestamp
);

