package com.seguimiento.seguimiento.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ResumenSeguimientoDTO {

    private Long solicitudId;
    private OffsetDateTime fechaHoraInicio;
    private OffsetDateTime fechaHoraFin;
    private String estadoActual;   // tipo del último evento, ej: "ENTREGADO"
    private Integer totalEventos;
}
