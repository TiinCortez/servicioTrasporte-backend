package com.tarifas.tarifa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculoResponseDTO {

    // El único dato que le devolveremos a 'ms-transporte'
    private BigDecimal costoEstimado;
}