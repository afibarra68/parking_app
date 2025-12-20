-- Agregar columna printer_printer_id a ticket_template para relacionar con printer
ALTER TABLE printer ADD COLUMN IF NOT EXISTS paper_type varchar NULL;