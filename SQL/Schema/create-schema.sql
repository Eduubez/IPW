create table Role
(
    name        varchar(255) not null primary key check (name in ('admin', 'investigator', 'manager', 'inserter')),
    permissions varchar(4)   not null
);


create table User_Role
(
    user_id   int          not null references Users (id),
    role_name varchar(255) not null references Role (name),
    primary key (user_id, role_name)
);

create table Users
(
    id                  serial primary key,
    name                varchar(255) not null,
    password_validation varchar(255) not null
);

create table Token
(
    token      varchar(255) not null primary key,
    user_id    int          not null references Users (id),
    expires_at timestamp    not null,
    created_at timestamp    not null default current_timestamp
);

create table Process
(
    id           serial primary key,
    localization varchar(255) not null,
    name         varchar(255) not null,
    date         timestamp    not null default current_timestamp,
    area         varchar(255) not null
);


create table State
(
    process_id int          not null references Process (id),
    name       varchar(255) not null check (name in ('created', 'dispatched', 'ongoing', 'waiting_approval', 'approved',
                                                     'rejected')),
    start_date timestamp    not null default current_timestamp,
    end_date   timestamp             default null
);

create table Attachment
(
    id         serial primary key,
    process_id int          not null references Process (id),
    file_name  varchar(255) not null,
    file_type  varchar(255) not null,
    content    bytea        not null
);

create table Report
(
    process_id int  not null references Process (id),
    content    text not null

);
