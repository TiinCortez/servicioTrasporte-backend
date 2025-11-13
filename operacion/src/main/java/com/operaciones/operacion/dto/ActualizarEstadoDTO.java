package com.operaciones.operacion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActualizarEstadoDTO {
    
    @NotBlank(message = "El estado no puede estar vacío")
    private String estado;
}