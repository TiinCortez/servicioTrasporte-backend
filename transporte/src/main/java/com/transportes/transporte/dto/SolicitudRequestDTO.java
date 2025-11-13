package com.transportes.transporte.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SolicitudRequestDTO {

    @NotNull(message = "El ID del cliente no puede ser nulo")
    private Long clienteId;

    @NotNull(message = "El ID del contenedor no puede ser nulo")
    private Long contenedorId;

    @NotBlank(message = "La dirección de origen es obligatoria")
    private String origenDir; // El usuario envía la dirección

    @NotBlank(message = "La dirección de destino es obligatoria")
    private String destinoDir; // El usuario envía la dirección
}