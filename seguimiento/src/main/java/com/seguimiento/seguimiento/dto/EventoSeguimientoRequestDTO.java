package com.seguimiento.seguimiento.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EventoSeguimientoRequestDTO {

    @NotBlank
    private String tipo;

    // opcional, cuando el evento está asociado a un tramo
    private Long tramoId;

    private String descripcion;

    private Double lat;
    private Double lng;
}
