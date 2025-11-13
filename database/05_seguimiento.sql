SET search_path TO seguimiento, public;

-- Evento de tracking (coincide con tu entidad ms-seguimiento)
CREATE TABLE IF NOT EXISTS evento_seguimiento (
  id           BIGSERIAL PRIMARY KEY,
  tipo         varchar(30) NOT NULL,
  solicitud_id BIGSERIAL NOT NULL,
  tramo_id     BIGSERIAL,
  fecha_hora   timestamptz NOT NULL DEFAULT now(),
  descripcion  varchar(300),
  lat          double precision,
  lng          double precision
);

CREATE INDEX IF NOT EXISTS idx_evento_sol ON evento_seguimiento (solicitud_id, fecha_hora);
CREATE INDEX IF NOT EXISTS idx_evento_tramo ON evento_seguimiento (tramo_id, fecha_hora);


