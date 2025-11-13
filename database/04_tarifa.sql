CREATE SCHEMA IF NOT EXISTS tarifa;
SET search_path TO tarifa, public;

-- Tarifas y recargos
CREATE TABLE IF NOT EXISTS tarifa_base (
  id              BIGSERIAL PRIMARY KEY,
  nombre          varchar(80) NOT NULL,
  precio_base     numeric(14,2) NOT NULL,   
  precio_por_km   numeric(14,2) NOT NULL  
);

CREATE TABLE IF NOT EXISTS recargo (
  id      BIGSERIAL PRIMARY KEY,
  nombre  varchar(80) NOT NULL,
  tipo    varchar(12) NOT NULL,  -- FIJO | PORCENTAJE
  valor   numeric(14,4) NOT NULL
);

INSERT INTO tarifa_base (id, nombre, precio_base, precio_por_km) 
VALUES (1, 'Tarifa General', 5000.00, 150.00)
ON CONFLICT (id) DO NOTHING;

INSERT INTO recargo (nombre, tipo, valor) VALUES
('Seguro de Carga', 'FIJO', 1500.00),
('Recargo Combustible', 'PORCENTAJE', 5.0);