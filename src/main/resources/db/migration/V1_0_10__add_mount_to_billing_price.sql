-- Add mount column to billing_price table
ALTER TABLE billing_price 
ADD COLUMN IF NOT EXISTS mount BIGINT NULL;

