-- Cambiar billing_price de rangos (start/end) a horas exactas
-- Agregar columna hours
ALTER TABLE billing_price ADD COLUMN IF NOT EXISTS hours int4 NULL;

-- Migrar datos: usar el valor de 'end' como horas (asumiendo que start siempre fue 1)
-- Si hay datos existentes, migrar el rango máximo (end) como horas
UPDATE billing_price 
SET hours = "end" 
WHERE hours IS NULL AND "end" IS NOT NULL;

-- Si no hay end, usar start como horas
UPDATE billing_price 
SET hours = "start" 
WHERE hours IS NULL AND "start" IS NOT NULL;

-- Hacer hours NOT NULL después de migrar
ALTER TABLE billing_price ALTER COLUMN hours SET NOT NULL;

-- Crear índice único para evitar duplicados de horas por empresa y tipo de vehículo
CREATE UNIQUE INDEX IF NOT EXISTS idx_billing_price_unique_hours 
    ON billing_price(company_company_id, vehicule_type, hours) 
    WHERE status = 'ACTIVE';

-- Crear índice para búsquedas rápidas por horas
CREATE INDEX IF NOT EXISTS idx_billing_price_hours 
    ON billing_price(company_company_id, vehicule_type, hours, status);

-- Comentarios para documentación
COMMENT ON COLUMN billing_price.hours IS 'Número de horas transcurridas para esta tarifa';
COMMENT ON COLUMN billing_price."start" IS 'DEPRECATED: Usar hours en su lugar';
COMMENT ON COLUMN billing_price."end" IS 'DEPRECATED: Usar hours en su lugar';

