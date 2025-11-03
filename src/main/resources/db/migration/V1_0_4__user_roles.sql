CREATE SEQUENCE role_id_sequence START WITH 1 INCREMENT BY 1;

create table if not exists user_role
(
    user_role_id       bigint primary key not null,
    user_user_id            bigint             not null,
    role               varchar(100)       not null
    );

alter table user_role
    add constraint FK_user_id_role foreign key (user_user_id) references app_user (app_user_id);
