
-- Crear la tabla business_service
CREATE TABLE business_service (
	business_service_id int8 NOT NULL,
	principal_name varchar(50) NOT NULL,
	description varchar(120) NULL,
	code VARCHAR NOT NULL,
	created_date timestamp
);

ALTER TABLE business_service ADD CONSTRAINT business_service_key PRIMARY KEY (business_service_id);

CREATE TABLE country (
	country_id int8 NOT NULL,
	"name" varchar(50) NULL,
	description varchar(50) NULL,
	iso_code varchar(5) NULL,
	timezone varchar(50) NULL,
	lang varchar(50) NULL,
	currency varchar(10) NULL
);

CREATE SEQUENCE country_country_id_seq 	INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 	CACHE 1 	NO CYCLE;
alter TABLE country	ADD CONSTRAINT country_pkey PRIMARY KEY (country_id);

CREATE TABLE company (
	company_id int8 NOT NULL,
	company_name varchar(50) NULL,
	number_identity varchar(20) NULL,
	country_country_id int8 NULL
);

alter table company add	CONSTRAINT company_pkey PRIMARY KEY (company_id);
alter table company add CONSTRAINT fk_country_country_id FOREIGN KEY (country_country_id) REFERENCES country(country_id);

CREATE TABLE company_business_service (
	company_business_service_id int8 NOT NULL,
	company_company_id int8 NULL,
	business_service_business_service_id int8 NULL,
	created_date timestamp NULL
);

ALTER TABLE company_business_service
    ADD CONSTRAINT company_business_service_pkey PRIMARY KEY (company_business_service_id);

ALTER TABLE company_business_service ADD CONSTRAINT fk_company_company_id
    FOREIGN KEY (company_company_id) REFERENCES company(company_id);

ALTER TABLE company_business_service ADD CONSTRAINT fk_business_service_id
    FOREIGN KEY (business_service_business_service_id) REFERENCES business_service(business_service_id);

CREATE TABLE app_user (
	app_user_id int8 NOT NULL,
	company_company_id int8 NULL,
	company_name varchar(50) NULL,
	first_name varchar(50) not null,
	last_name varchar(20),
	second_name varchar(20) not null,
	second_lastname varchar(20),
	number_identity varchar(20) NULL,
	processor_id varchar(50) NULL,
	sha varchar(100) NULL,
	"password" varchar(100) NULL,
	salt varchar(100) NULL,
	access_credential varchar(100) NULL,
	access_limit date NULL
);

alter TABLE app_user add CONSTRAINT app_user_pkey PRIMARY KEY (app_user_id);
alter TABLE app_user add CONSTRAINT fk_company_company_id FOREIGN KEY (company_company_id) REFERENCES company(company_id);
CREATE SEQUENCE IF NOT EXISTS user_user_id_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10 NO CYCLE;

create table if not exists user_role
(
    user_role_id       bigint primary key not null,
    user_user_id            bigint             not null,
    role               varchar(100)       not null
    );

CREATE SEQUENCE role_id_sequence START WITH 1 INCREMENT BY 1;

alter table user_role add constraint FK_user_id_role foreign key (user_user_id) references app_user (app_user_id);

CREATE TABLE client (
	client_id int8 NOT NULL,
	full_name varchar(50) NULL,
	number_identity varchar(20) NULL,
	payment_day date NULL,
	client_company_id int8 NULL
);

ALTER TABLE client ADD CONSTRAINT client_pkey PRIMARY KEY (client_id);
ALTER TABLE client ADD CONSTRAINT fk_company_client_company FOREIGN KEY (client_company_id) REFERENCES company(company_id);