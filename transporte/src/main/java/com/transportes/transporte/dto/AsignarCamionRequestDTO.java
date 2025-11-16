package com.transportes.transporte.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AsignarCamionRequestDTO {

    @NotNull
    private Long camionId;   // id del camión a asignar (de Operaciones)
}
