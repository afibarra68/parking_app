-- Eliminar la columna printer_type de ticket_template
-- El tipo de impresora ahora se obtiene de la relación con printer
ALTER TABLE ticket_template DROP COLUMN IF EXISTS printer_type;

-- Eliminar el índice relacionado si existe
DROP INDEX IF EXISTS idx_ticket_template_printer_type;

