package com.transportes.transporte.dto;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class ContenedorDTO {
    private Long id;
    private String codigo;
    private Integer capacidadKg;
    private String estado;
    private OffsetDateTime creadoEn;
    private Long clienteId;
}
