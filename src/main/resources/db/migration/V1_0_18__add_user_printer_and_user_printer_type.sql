-- Crear la tabla user_printer (relación muchos a muchos entre usuarios e impresoras)
CREATE TABLE user_printer (
	user_printer_id int8 NOT NULL,
	user_user_id int8 NOT NULL,
	printer_printer_id int8 NOT NULL,
	is_active boolean DEFAULT true,
	created_date timestamp NULL,
	updated_date timestamp NULL
);

ALTER TABLE user_printer ADD CONSTRAINT user_printer_pkey PRIMARY KEY (user_printer_id);

-- Crear secuencia para user_printer
CREATE SEQUENCE IF NOT EXISTS user_printer_user_printer_id_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10 NO CYCLE;

-- Agregar foreign keys para user_printer
ALTER TABLE user_printer ADD CONSTRAINT fk_user_printer_user_user_id 
    FOREIGN KEY (user_user_id) REFERENCES app_user(app_user_id);
    
ALTER TABLE user_printer ADD CONSTRAINT fk_user_printer_printer_printer_id 
    FOREIGN KEY (printer_printer_id) REFERENCES printer(printer_id);

-- Crear índice único para evitar duplicados
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_printer_unique 
    ON user_printer(user_user_id, printer_printer_id);

-- Crear la tabla user_printer_type (habilitar tipos de impresoras a usuarios)
CREATE TABLE user_printer_type (
	user_printer_type_id int8 NOT NULL,
	user_user_id int8 NOT NULL,
	printer_type varchar(20) NOT NULL, -- COM, WINDOWS, NETWORK
	is_enabled boolean DEFAULT true,
	created_date timestamp NULL,
	updated_date timestamp NULL
);

ALTER TABLE user_printer_type ADD CONSTRAINT user_printer_type_pkey PRIMARY KEY (user_printer_type_id);

-- Crear secuencia para user_printer_type
CREATE SEQUENCE IF NOT EXISTS user_printer_type_user_printer_type_id_seq INCREMENT BY 1 START WITH 1 MINVALUE 1 CACHE 10 NO CYCLE;

-- Agregar foreign keys para user_printer_type
ALTER TABLE user_printer_type ADD CONSTRAINT fk_user_printer_type_user_user_id 
    FOREIGN KEY (user_user_id) REFERENCES app_user(app_user_id);

-- Crear índice único para evitar duplicados
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_printer_type_unique 
    ON user_printer_type(user_user_id, printer_type);

-- Agregar índices para optimizar consultas
CREATE INDEX IF NOT EXISTS idx_user_printer_user_id ON user_printer(user_user_id);
CREATE INDEX IF NOT EXISTS idx_user_printer_printer_id ON user_printer(printer_printer_id);
CREATE INDEX IF NOT EXISTS idx_user_printer_active ON user_printer(is_active);

CREATE INDEX IF NOT EXISTS idx_user_printer_type_user_id ON user_printer_type(user_user_id);
CREATE INDEX IF NOT EXISTS idx_user_printer_type_type ON user_printer_type(printer_type);
CREATE INDEX IF NOT EXISTS idx_user_printer_type_enabled ON user_printer_type(is_enabled);

