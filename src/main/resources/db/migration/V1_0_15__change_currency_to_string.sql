-- Cambiar el tipo de columna currency de float8 a varchar en open_transaction
ALTER TABLE open_transaction 
ALTER COLUMN currency TYPE VARCHAR(10) USING CASE 
    WHEN currency IS NULL THEN NULL 
    ELSE currency::TEXT 
END;

-- Cambiar el tipo de columna currency de float8 a varchar en closed_transaction
ALTER TABLE closed_transaction 
ALTER COLUMN currency TYPE VARCHAR(10) USING CASE 
    WHEN currency IS NULL THEN NULL 
    ELSE currency::TEXT 
END;

