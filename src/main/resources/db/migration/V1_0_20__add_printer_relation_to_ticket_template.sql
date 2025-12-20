-- Agregar columna printer_printer_id a ticket_template para relacionar con printer
ALTER TABLE ticket_template ADD COLUMN IF NOT EXISTS printer_printer_id int8 NULL;

-- Agregar foreign key para la relación con printer
ALTER TABLE ticket_template ADD CONSTRAINT fk_ticket_template_printer_printer_id 
    FOREIGN KEY (printer_printer_id) REFERENCES printer(printer_id);

-- Agregar índice para optimizar consultas por impresora
CREATE INDEX IF NOT EXISTS idx_ticket_template_printer_id ON ticket_template(printer_printer_id);

