package com.tarifas.tarifa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TarifaBaseDTO {
    private Long id;

    @NotBlank
    private String nombre;
    
    @NotNull
    private BigDecimal precioBase;
    
    @NotNull
    private BigDecimal precioPorKm;
}