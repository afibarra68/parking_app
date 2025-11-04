
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

