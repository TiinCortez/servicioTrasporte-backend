CREATE SCHEMA IF NOT EXISTS seguimiento;
SET search_path TO seguimiento, public;

CREATE TABLE IF NOT EXISTS evento_seguimiento (
  id           BIGSERIAL PRIMARY KEY,
  tipo         varchar(30) NOT NULL,
  solicitud_id BIGINT NOT NULL,  -- <- referencia lógica a transporte.solicitud(id)
  tramo_id     BIGINT,           -- <- referencia lógica a transporte.tramo(id)
  fecha_hora   timestamptz NOT NULL DEFAULT now(),
  descripcion  varchar(300),
  lat          double precision,
  lng          double precision
);

CREATE INDEX IF NOT EXISTS idx_evento_sol ON evento_seguimiento (solicitud_id, fecha_hora);
CREATE INDEX IF NOT EXISTS idx_evento_tramo ON evento_seguimiento (tramo_id, fecha_hora);
SET search_path TO seguimiento, public;

INSERT INTO evento_seguimiento (tipo, solicitud_id, tramo_id, descripcion, lat, lng)
VALUES
('SOLICITUD_CREADA', 1, NULL,
 'La solicitud fue creada en el sistema',
 -31.4167, -64.1833),

('INICIO_TRAMO', 1, 1,
 'El camión inició el primer tramo desde el depósito de Córdoba',
 -31.4167, -64.1833),

('FIN_TRAMO', 1, 1,
 'El camión finalizó el primer tramo',
 -32.8908, -68.8272),

('ENTREGADO', 1, NULL,
 'El contenedor fue entregado al cliente en destino',
 -34.6037, -58.3816);