-- Add vehicule_type column to billing_price table
ALTER TABLE billing_price 
ADD COLUMN IF NOT EXISTS vehicule_type VARCHAR(20) NULL;

