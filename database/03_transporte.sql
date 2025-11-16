-- Le decimos a Postgres que cree el schema si no existe
CREATE SCHEMA IF NOT EXISTS transporte;

-- Seteamos este schema como el de defecto para las siguientes tablas
SET search_path TO transporte, public;

-- TABLA PRINCIPAL DEL MICROSERVICIO
CREATE TABLE IF NOT EXISTS solicitud (
  id                  BIGSERIAL PRIMARY KEY,
  numero_solicitud    varchar(40) UNIQUE NOT NULL,
  cliente_id          BIGINT NOT NULL,      -- Ref. blanda a operacion.cliente
  contenedor_id       BIGINT NOT NULL,    -- Ref. blanda a operacion.contenedor
  origen_lat          double precision,
  origen_lng          double precision,
  origen_dir          varchar(200),
  destino_lat         double precision,
  destino_lng         double precision,
  destino_dir         varchar(200),
  estado              varchar(20) NOT NULL DEFAULT 'PENDIENTE',
  costo_estimado      numeric(14,2),
  tiempo_estimado_min integer,
  costo_final         numeric(14,2),
  tiempo_real_min     integer,
  creado_en           timestamptz NOT NULL DEFAULT now()
  -- peso y volumen del contenedor se obtienen de operacion.contenedor
);

-- Tramos de cada solicitud
CREATE TABLE IF NOT EXISTS tramo (
  id                    BIGSERIAL PRIMARY KEY,
  solicitud_id          BIGINT NOT NULL REFERENCES solicitud(id),
  estado                varchar(20) NOT NULL DEFAULT 'PENDIENTE',
  camion_id             BIGINT,             -- Ref. blanda a operacion.camion
  deposito_origen_id    BIGINT,             -- Ref. blanda a operacion.deposito
  nombre_deposito_origen varchar(100),
  deposito_destino_id   BIGINT,             -- Ref. blanda a operacion.deposito
  nombre_deposito_destino varchar(100),
  origen_dir            varchar(200),
  destino_dir           varchar(200),
  distancia_km          numeric(10,2),
  duracion_min          integer,
  costo_real_tramo      numeric(14,2),
  fecha_hora_inicio     timestamptz,
  fecha_hora_fin        timestamptz
);