-- Agregar columna business_service_business_service_id a billing_price
ALTER TABLE billing_price 
ADD COLUMN IF NOT EXISTS business_service_business_service_id BIGINT;

-- Eliminar constraint si existe antes de crearlo
ALTER TABLE billing_price
DROP CONSTRAINT IF EXISTS fk_billing_price_business_service;

-- Agregar foreign key constraint
ALTER TABLE billing_price
ADD CONSTRAINT fk_billing_price_business_service 
FOREIGN KEY (business_service_business_service_id) 
REFERENCES business_service(business_service_id);

-- Crear índice para búsqueda rápida
CREATE INDEX IF NOT EXISTS idx_billing_price_business_service 
ON billing_price(business_service_business_service_id);

-- Eliminar la columna business_service_business_service_id de open_transaction si existe
ALTER TABLE open_transaction 
DROP COLUMN IF EXISTS business_service_business_service_id;

-- Eliminar constraint de open_transaction si existe
ALTER TABLE open_transaction
DROP CONSTRAINT IF EXISTS fk_open_transaction_business_service;

-- Eliminar índice de open_transaction si existe
DROP INDEX IF EXISTS idx_open_transaction_business_service;

