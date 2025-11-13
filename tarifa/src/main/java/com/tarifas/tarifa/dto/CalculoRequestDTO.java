package com.tarifas.tarifa.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CalculoRequestDTO {

    // Dato que nos va a enviar el servicio de transporte
    @NotNull
    @Positive
    private BigDecimal distanciaKm;
    
    // (En el futuro, podríamos agregar peso, volumen, etc.)
}