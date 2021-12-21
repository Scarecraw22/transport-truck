create schema tt;

drop table if exists tt.phone_number;
create table tt.phone_number (
    id bigserial primary key,
    phone_prefix varchar(3),
    phone_number varchar(15),
    updated_at timestamp with time zone,
    created_at timestamp with time zone not null,
    unique (phone_prefix, phone_number)
);

drop table if exists tt.user_details;
create table tt.users (
    id bigserial primary key,
    username varchar(30) not null,
    password varchar(255) not null,
    salt text not null,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    address varchar(100),
    email varchar(50) not null,
    role varchar(20) not null,
    updated_at timestamp with time zone,
    created_at timestamp with time zone not null
);

drop table if exists tt.user_phone_number;
create table tt.user_phone_number (
    user_id bigint not null,
    phone_number_id bigint not null,
    constraint fk_user foreign key (user_id) references tt.users(id),
    constraint fk_phone_number foreign key (phone_number_id) references tt.phone_number(id),
    unique (user_id, phone_number_id)
);

drop table if exists tt.job;
create table tt.job (
    id bigserial primary key,
    customer_id bigserial not null,
    title varchar(100) not null,
    description varchar(255) not null,
    source_address varchar(100) not null,
    destination_address varchar(100) not null,
    destination_email varchar(50),
    updated_at timestamp with time zone,
    created_at timestamp with time zone not null,
    constraint fk_customer foreign key (customer_id) references tt.users(id)
);

drop table if exists tt.job_phone_number;
create table tt.job_phone_number (
    job_id bigint not null,
    phone_number_id bigint not null,
    constraint fk_job foreign key (job_id) references tt.job(id),
    constraint fk_phone_number foreign key (phone_number_id) references tt.phone_number(id),
    unique (job_id, phone_number_id)
);

drop table if exists tt.finished_job;
create table tt.finished_job (
    id bigserial primary key,
    job_id bigserial not null,
    driver_id bigserial not null,
    finished_at timestamp with time zone,
    updated_at timestamp with time zone,
    created_at timestamp with time zone not null,
    constraint fk_job foreign key (job_id) references tt.job(id),
    constraint fk_driver foreign key (driver_id) references tt.users(id)
);

drop table if exists tt.assigned_job;
create table tt.assigned_job (
    id bigserial primary key,
    job_id bigserial not null,
    driver_id bigserial not null,
    in_progress boolean not null,
    assigned_at timestamp with time zone not null,
    updated_at timestamp with time zone,
    created_at timestamp with time zone not null,
    constraint fk_job foreign key (job_id) references tt.job(id),
    constraint fk_driver foreign key (driver_id) references tt.users(id)
);