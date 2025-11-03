
CREATE SEQUENCE country_country_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;-- buisiness_service definition

CREATE TABLE buisiness_service (
	bussines_service_id int8 NOT NULL,
	"name" varchar(50) NULL,
	description varchar(20) NULL,
	country_country_id int8 NULL,
	CONSTRAINT buisiness_service_pkey PRIMARY KEY (bussines_service_id)
);

CREATE TABLE country (
	country_id int8 NOT NULL,
	"name" varchar(50) NULL,
	description varchar(50) NULL,
	iso_code varchar(5) NULL,
	timezone varchar(50) NULL,
	lang varchar(50) NULL,
	currency varchar(10) NULL,
	CONSTRAINT country_pkey PRIMARY KEY (country_id)
);

CREATE TABLE discount (
	discount_id int4 NOT NULL,
	percent_discount float8 NULL,
	name_discount varchar(50) NULL,
	discount_type varchar(20) NULL,
	applicable_discount bool NULL,
	status varchar(20) NULL,
	start_date date NULL,
	end_date date NULL,
	CONSTRAINT discount_pkey PRIMARY KEY (discount_id)
);

CREATE TABLE company (
	company_id int8 NOT NULL,
	company_name varchar(50) NULL,
	number_identity varchar(20) NULL,
	country_country_id int8 NULL,
	CONSTRAINT company_pkey PRIMARY KEY (company_id),
	CONSTRAINT fk_country_country_id FOREIGN KEY (country_country_id) REFERENCES country(country_id)
);

CREATE TABLE company_buisiness_service (
	bussines_service_id int8 NOT NULL,
	company_company_id int8 NULL,
	business_service_business_service_id int8 NULL,
	created_date timestamp NULL,
	CONSTRAINT company_buisiness_service_pkey PRIMARY KEY (bussines_service_id),
	CONSTRAINT fk_business_service_id FOREIGN KEY (business_service_business_service_id) REFERENCES buisiness_service(bussines_service_id),
	CONSTRAINT fk_company_company_id FOREIGN KEY (company_company_id) REFERENCES company(company_id)
);

CREATE TABLE app_user (
	app_user_id int8 NOT NULL,
	company_company_id int8 NULL,
	company_name varchar(50) NULL,
	number_identity varchar(20) NULL,
	processor_id varchar(50) NULL,
	sha varchar(100) NULL,
	"password" varchar(100) NULL,
	salt varchar(100) NULL,
	access_credential varchar(100) NULL,
	access_limit date NULL,
	CONSTRAINT app_user_pkey PRIMARY KEY (app_user_id),
	CONSTRAINT fk_company_company_id FOREIGN KEY (company_company_id) REFERENCES company(company_id)
);


-- billing_price definition

-- Drop table

-- DROP TABLE billing_price;

CREATE TABLE billing_price (
	billing_price_id int8 NOT NULL,
	status varchar(20) NULL,
	date_start_disabled date NULL,
	cover_type varchar(50) NULL,
	apply_discount bool NULL,
	discount_discount_id int4 NULL,
	company_company_id int8 NULL,
	"start" int4 NULL,
	"end" int4 NULL,
	CONSTRAINT billing_price_pkey PRIMARY KEY (billing_price_id),
	CONSTRAINT fk_company_company_company_id FOREIGN KEY (company_company_id) REFERENCES company(company_id),
	CONSTRAINT fk_discount_discount_discount_id FOREIGN KEY (discount_discount_id) REFERENCES discount(discount_id)
);


-- client definition

-- Drop table

-- DROP TABLE client;

CREATE TABLE client (
	client_id int8 NOT NULL,
	full_name varchar(50) NULL,
	number_identity varchar(20) NULL,
	payment_day date NULL,
	client_company_id int8 NULL,
	CONSTRAINT client_pkey PRIMARY KEY (client_id),
	CONSTRAINT fk_company_client_company FOREIGN KEY (client_company_id) REFERENCES company(company_id)
);


-- closed_transaction definition

-- Drop table

-- DROP TABLE closed_transaction;

CREATE TABLE closed_transaction (
	closed_transaction_id int8 NOT NULL,
	start_time time NULL,
	start_day date NULL,
	end_date time NULL,
	end_tyme date NULL,
	currency float8 NULL,
	company_company_id int8 NULL,
	status varchar(20) NULL,
	billing_price_billing_price_id int8 NULL,
	amount float8 NULL,
	discount varchar(50) NULL,
	total_amount float8 NULL,
	time_elapsed varchar(50) NULL,
	operation_date timestamp NULL,
	service_type_service_type_id int8 NULL,
	seller_app_user_id int8 NULL,
	seller_name varchar(50) NULL,
	contractor int8 NULL,
	CONSTRAINT closed_transaction_pkey PRIMARY KEY (closed_transaction_id),
	CONSTRAINT fk_app_user_id FOREIGN KEY (seller_app_user_id) REFERENCES app_user(app_user_id),
	CONSTRAINT fk_billing_price_id FOREIGN KEY (billing_price_billing_price_id) REFERENCES billing_price(billing_price_id),
	CONSTRAINT fk_company_id FOREIGN KEY (company_company_id) REFERENCES company(company_id)
);


-- montly definition

-- Drop table

-- DROP TABLE montly;

CREATE TABLE montly (
	montly_id int8 NOT NULL,
	vehicle_plate varchar(50) NULL,
	service_id varchar(20) NULL,
	service_name varchar(50) NULL,
	client_id int8 NULL,
	client_name varchar(50) NULL,
	payment_day date NULL,
	company_id int8 NULL,
	amount int8 NULL,
	CONSTRAINT montly_pkey PRIMARY KEY (montly_id),
	CONSTRAINT fk_client_client_id FOREIGN KEY (client_id) REFERENCES client(client_id)
);


-- open_transaction definition

-- Drop table

-- DROP TABLE open_transaction;

CREATE TABLE open_transaction (
	open_transaction_id int8 NOT NULL,
	start_time time NULL,
	start_day date NULL,
	end_date time NULL,
	end_tyme date NULL,
	currency float8 NULL,
	company_company_id int8 NULL,
	status varchar(20) NULL,
	billing_price_billing_price_id int8 NULL,
	amount float8 NULL,
	discount varchar(50) NULL,
	total_amount float8 NULL,
	time_elapsed varchar(50) NULL,
	operation_date timestamp NULL,
	service_type_service_type_id int8 NULL,
	app_user_app_user_seller int8 NULL,
	CONSTRAINT open_transaction_pkey PRIMARY KEY (open_transaction_id),
	CONSTRAINT fk_app_user_user_seller_const FOREIGN KEY (app_user_app_user_seller) REFERENCES app_user(app_user_id),
	CONSTRAINT fk_billing_price_const FOREIGN KEY (billing_price_billing_price_id) REFERENCES billing_price(billing_price_id),
	CONSTRAINT fk_company_id FOREIGN KEY (company_company_id) REFERENCES company(company_id)
);