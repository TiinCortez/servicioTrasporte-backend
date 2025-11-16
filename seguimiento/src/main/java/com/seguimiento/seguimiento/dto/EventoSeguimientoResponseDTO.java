package com.seguimiento.seguimiento.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class EventoSeguimientoResponseDTO {

    private Long id;
    private String tipo;
    private Long solicitudId;
    private Long tramoId;
    private OffsetDateTime fechaHora;
    private String descripcion;
    private Double lat;
    private Double lng;
}
