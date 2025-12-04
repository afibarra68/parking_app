-- Agregar índices para optimizar las consultas de estadísticas del día
-- Índice compuesto para consultas de transacciones cerradas del día por compañía
CREATE INDEX IF NOT EXISTS idx_closed_transaction_company_status_date 
ON closed_transaction(company_company_id, status, operation_date)
WHERE status = 'CLOSED';

-- Índice adicional para operation_date para consultas de rango de fechas
CREATE INDEX IF NOT EXISTS idx_closed_transaction_operation_date 
ON closed_transaction(operation_date);

-- Índice para company_company_id si no existe
CREATE INDEX IF NOT EXISTS idx_closed_transaction_company_id 
ON closed_transaction(company_company_id);

