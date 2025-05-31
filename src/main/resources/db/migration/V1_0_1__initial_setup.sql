create table if not exists users(
    user_id                 bigint primary key   not null,
    first_name              varchar         not null,
    hashed_password         varchar         null,
    second_name             varchar         null,
    last_name               varchar         null,
    second_last_name        varchar         null,
    phone_number            varchar         null,
    password_encrypt        varchar         null
                                );

CREATE SEQUENCE user_user_id_seq START WITH 1 INCREMENT BY 1;

create unique index users_phone_ux
    on users (phone_number);