package com.transportes.transporte.dto;

import lombok.Data;

@Data
public class TramoAsignadoResponseDTO {
    private Long id;
    private String estado;
    private Long camionId;

    public TramoAsignadoResponseDTO(Long id, String estado, Long camionId) {
        this.id = id;
        this.estado = estado;
        this.camionId = camionId;
    }
}
