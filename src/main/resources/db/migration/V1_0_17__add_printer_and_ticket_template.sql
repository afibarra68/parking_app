-- Crear la tabla printer
CREATE TABLE printer (
	printer_id int8 NOT NULL,
	printer_name varchar(100) NOT NULL,
	printer_type varchar(20) NULL, -- COM, WINDOWS, NETWORK
	connection_string varchar(255) NULL, -- COM port, printer name, or network address
	is_active boolean DEFAULT true,
	company_company_id int8 NULL,
	user_user_id int8 NULL,
	created_date timestamp NULL,
	updated_date timestamp NULL
);

ALTER TABLE printer ADD CONSTRAINT printer_pkey PRIMARY KEY (printer_id);

-- Crear secuencia para printer
CREATE SEQUENCE IF NOT EXISTS printer_printer_id_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10 NO CYCLE;

-- Agregar foreign keys para printer
ALTER TABLE printer ADD CONSTRAINT fk_printer_company_company_id 
    FOREIGN KEY (company_company_id) REFERENCES company(company_id);
    
ALTER TABLE printer ADD CONSTRAINT fk_printer_user_user_id 
    FOREIGN KEY (user_user_id) REFERENCES app_user(app_user_id);

-- Crear la tabla ticket_template
CREATE TABLE ticket_template (
	ticket_template_id int8 NOT NULL,
	template text NULL, -- tirilla - contenido del template ESC/POS
	printer_type varchar(20) NULL, -- tipo de impresora para imprimir (COM, WINDOWS, NETWORK)
	ticket_type varchar(50) NULL, -- tipo de tirilla (INGRESO, SALIDA, FACTURA, COMPROBANTE_INGRESO)
	invoice text NULL, -- factura - template para facturas
	entry_receipt text NULL, -- comprobante de ingreso - template para comprobantes de ingreso
	company_company_id int8 NULL,
	user_user_id int8 NULL,
	created_date timestamp NULL,
	updated_date timestamp NULL
);

ALTER TABLE ticket_template ADD CONSTRAINT ticket_template_pkey PRIMARY KEY (ticket_template_id);

-- Crear secuencia para ticket_template
CREATE SEQUENCE IF NOT EXISTS ticket_template_ticket_template_id_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10 NO CYCLE;

-- Agregar foreign keys para ticket_template
ALTER TABLE ticket_template ADD CONSTRAINT fk_ticket_template_company_company_id 
    FOREIGN KEY (company_company_id) REFERENCES company(company_id);
    
ALTER TABLE ticket_template ADD CONSTRAINT fk_ticket_template_user_user_id 
    FOREIGN KEY (user_user_id) REFERENCES app_user(app_user_id);

-- Agregar índices para optimizar consultas
CREATE INDEX IF NOT EXISTS idx_printer_company_id ON printer(company_company_id);
CREATE INDEX IF NOT EXISTS idx_printer_user_id ON printer(user_user_id);
CREATE INDEX IF NOT EXISTS idx_printer_type ON printer(printer_type);
CREATE INDEX IF NOT EXISTS idx_printer_active ON printer(is_active);

CREATE INDEX IF NOT EXISTS idx_ticket_template_company_id ON ticket_template(company_company_id);
CREATE INDEX IF NOT EXISTS idx_ticket_template_user_id ON ticket_template(user_user_id);
CREATE INDEX IF NOT EXISTS idx_ticket_template_printer_type ON ticket_template(printer_type);
CREATE INDEX IF NOT EXISTS idx_ticket_template_ticket_type ON ticket_template(ticket_type);

