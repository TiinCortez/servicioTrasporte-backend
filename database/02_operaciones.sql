CREATE SCHEMA IF NOT EXISTS operaciones;
SET search_path TO operaciones, public;

-- --- 1. CREACIÓN DE TABLAS ---
-- (Tablas "padre" primero, que no dependen de otras)

CREATE TABLE IF NOT EXISTS cliente (
  id           BIGSERIAL PRIMARY KEY,
  razon_social varchar(120) NOT NULL,
  cuit         varchar(20) UNIQUE NOT NULL,
  direccion    varchar(200),
  telefono     varchar(40),
  email        varchar(80)
);

CREATE TABLE IF NOT EXISTS deposito (
  id                   BIGSERIAL PRIMARY KEY,
  nombre               varchar(80) NOT NULL,
  direccion            varchar(200),
  lat                  double precision,
  lng                  double precision,
  costo_estadia_diario numeric(12,2),
  creado_en            timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS camion (
  id                   BIGSERIAL PRIMARY KEY,
  dominio              varchar(20) UNIQUE NOT NULL,
  nombre_transportista varchar(120) NOT NULL,
  telefono             varchar(40),
  cap_peso_kg          numeric(12,2) NOT NULL,
  cap_vol_m3           numeric(12,2) NOT NULL,
  costo_base_km        numeric(12,2) NOT NULL,
  consumo_100km        numeric(8,3) NOT NULL,
  disponible           boolean NOT NULL DEFAULT true
);

-- (Tabla "hija" al final, porque depende de 'cliente')
CREATE TABLE IF NOT EXISTS contenedor (
  id           BIGSERIAL PRIMARY KEY,
  codigo       varchar(40) UNIQUE NOT NULL,
  capacidad_kg integer NOT NULL,
  estado       varchar(20) NOT NULL DEFAULT 'PENDIENTE', -- PENDIENTE | EN_CURSO | FINALIZADO
  creado_en    timestamptz NOT NULL DEFAULT now(),
  cliente_id   BIGINT NOT NULL,
  CONSTRAINT fk_contenedor_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);


-- CREACION DE DATOS DE CLIENTES --
INSERT INTO cliente (id, razon_social, cuit) VALUES
(1, 'Cliente de Prueba', '20-12345678-9'),
(2, 'Cliente Dos', '20-87654321-9'); -- <- Agregado para que el INSERT de contenedor no falle
SELECT setval('cliente_id_seq', (SELECT MAX(id) FROM cliente));

-- CREACION DE DATOS DE CAMIONES --
INSERT INTO camion (id, dominio, nombre_transportista, cap_peso_kg, cap_vol_m3, costo_base_km, consumo_100km) VALUES
(1, 'AB123CD','JOSE HERNANDEZ', 25000, 80, 150.50, 30.5),
(2, 'EF456GH','MARIO GOMEZ', 20000, 60, 130.75, 25.0),
(3, 'IJ789KL','LUIS PEREZ', 30000, 100, 180.00, 35.2),
(4, 'MN012OP','ANA LOPEZ', 22000, 70, 140.25, 28.3);
SELECT setval('camion_id_seq', (SELECT MAX(id) FROM camion));

-- CREACION DE DATOS DE DEPOSITOS [UNO POR PROVINCIA] --
INSERT INTO deposito (id, nombre, direccion, lat, lng, creado_en, costo_estadia_diario) VALUES
(1,  'DEP-001', 'Plaza de Mayo, Ciudad Autónoma de Buenos Aires, CABA', -34.6083, -58.3722, now(), 2500.00),
(2,  'DEP-002', 'Plaza Moreno, La Plata, Buenos Aires', -34.9213, -57.9545, now(), 2500.00),
(3,  'DEP-003', 'Plaza 25 de Mayo, San Fernando del Valle de Catamarca, Catamarca', -28.4686, -65.7795, now(), 2500.00),
(4,  'DEP-004', 'Plaza 25 de Mayo, Resistencia, Chaco', -27.4516, -58.9862, now(), 2500.00),
(5,  'DEP-005', 'Plaza 25 de Mayo, Rawson, Chubut', -43.3004, -65.1022, now(), 2500.00),
(6,  'DEP-006', 'Plaza 25 de Mayo, Córdoba, Córdoba', -31.4167, -64.1833, now(), 2500.00),
(7,  'DEP-007', 'Plaza 25 de Mayo, Corrientes, Corrientes', -27.4806, -58.8345, now(), 2500.00),
(8,  'DEP-008', 'Plaza 9 de Julio, Paraná, Entre Ríos', -31.7310, -60.5238, now(), 2500.00),
(9,  'DEP-009', 'Plaza San Martín, Formosa, Formosa', -26.1842, -58.1789, now(), 2500.00),
(10, 'DEP-010', 'Plaza 9 de Julio, San Salvador de Jujuy, Jujuy', -24.1858, -65.2995, now(), 2500.00),
(11, 'DEP-011', 'Plaza San Martín, Santa Rosa, La Pampa', -36.6163, -64.2858, now(), 2500.00),
(12, 'DEP-012', 'Plaza 25 de Mayo, La Rioja, La Rioja', -29.4133, -66.8550, now(), 2500.00),
(13, 'DEP-013', 'Plaza 9 de Julio, Mendoza, Mendoza', -32.8908, -68.8272, now(), 2500.00),
(14, 'DEP-014', 'Plaza 9 de Julio, Posadas, Misiones', -27.3630, -55.8963, now(), 2500.00),
(15, 'DEP-015', 'Plaza San Martín, Neuquén, Neuquén', -38.9516, -68.0590, now(), 2500.00),
(16, 'DEP-016', 'Plaza 25 de Mayo, Viedma, Río Negro', -40.8134, -62.9969, now(), 2500.00),
(17, 'DEP-017', 'Plaza 25 de Mayo, Salta, Salta', -24.7889, -65.4100, now(), 2500.00),
(18, 'DEP-018', 'Plaza 9 de Julio, San Juan, San Juan', -31.5375, -68.5364, now(), 2500.00),
(19, 'DEP-019', 'Plaza Pringles, San Luis, San Luis', -33.3006, -66.3378, now(), 2500.00),
(20, 'DEP-020', 'Plaza 25 de Mayo, Santa Fe, Santa Fe', -31.6327, -60.7007, now(), 2500.00),
(21, 'DEP-021', 'Plaza San Martín, Santiago del Estero, Santiago del Estero', -27.7956, -64.2611, now(), 2500.00),
(22, 'DEP-022', 'Plaza Independencia, San Miguel de Tucumán, Tucumán', -26.8322, -65.2038, now(), 2500.00),
(23, 'DEP-023', 'Plaza San Martín, Río Gallegos, Santa Cruz', -51.6236, -69.2168, now(), 2500.00),
(24, 'DEP-024', 'Plaza San Martín, Ushuaia, Tierra del Fuego', -54.8019, -68.3029, now(), 2500.00);
SELECT setval('deposito_id_seq', (SELECT MAX(id) FROM deposito));

INSERT INTO contenedor (id, codigo, capacidad_kg, estado, creado_en, cliente_id) VALUES
(1, 'AAA-001', 10000, 'PENDIENTE', now(), 1),
(2, 'AAA-002', 10000, 'PENDIENTE', now(), 2),
(3, 'AAA-003', 12500, 'EN_CURSO', now(), 1),
(4, 'AAA-004', 12500, 'FINALIZADO', now(), 2),
(5, 'AAA-005', 25000, 'FINALIZADO', now(), 1);
SELECT setval('contenedor_id_seq', (SELECT MAX(id) FROM contenedor));