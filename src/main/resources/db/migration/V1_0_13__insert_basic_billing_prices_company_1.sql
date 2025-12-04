-- Insert basic billing prices for company 1
-- Ranges: 1-5, 6-8, 9-23 for all vehicle types

-- AUTOMOVIL
INSERT INTO billing_price (billing_price_id, status, company_company_id, "start", "end", mount, vehicule_type)
VALUES 
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 1, 5, 5000, 'AUTOMOVIL'),
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 6, 8, 8000, 'AUTOMOVIL'),
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 9, 23, 12000, 'AUTOMOVIL');

-- MOTOCICLETA
INSERT INTO billing_price (billing_price_id, status, company_company_id, "start", "end", mount, vehicule_type)
VALUES 
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 1, 5, 3000, 'MOTOCICLETA'),
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 6, 8, 5000, 'MOTOCICLETA'),
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 9, 23, 8000, 'MOTOCICLETA');

-- CAMIONETA
INSERT INTO billing_price (billing_price_id, status, company_company_id, "start", "end", mount, vehicule_type)
VALUES 
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 1, 5, 6000, 'CAMIONETA'),
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 6, 8, 10000, 'CAMIONETA'),
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 9, 23, 15000, 'CAMIONETA');

-- CAMION
INSERT INTO billing_price (billing_price_id, status, company_company_id, "start", "end", mount, vehicule_type)
VALUES 
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 1, 5, 8000, 'CAMION'),
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 6, 8, 13000, 'CAMION'),
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 9, 23, 20000, 'CAMION');

-- BICICLETA
INSERT INTO billing_price (billing_price_id, status, company_company_id, "start", "end", mount, vehicule_type)
VALUES 
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 1, 5, 2000, 'BICICLETA'),
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 6, 8, 3500, 'BICICLETA'),
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 9, 23, 5000, 'BICICLETA');

-- TRILER
INSERT INTO billing_price (billing_price_id, status, company_company_id, "start", "end", mount, vehicule_type)
VALUES 
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 1, 5, 10000, 'TRILER'),
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 6, 8, 16000, 'TRILER'),
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 9, 23, 25000, 'TRILER');

-- TRACTOMULA
INSERT INTO billing_price (billing_price_id, status, company_company_id, "start", "end", mount, vehicule_type)
VALUES 
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 1, 5, 12000, 'TRACTOMULA'),
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 6, 8, 20000, 'TRACTOMULA'),
    (nextval('billing_price_id_seq'), 'ACTIVE', 1, 9, 23, 30000, 'TRACTOMULA');

