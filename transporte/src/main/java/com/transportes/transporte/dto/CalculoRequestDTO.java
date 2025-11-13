package com.transportes.transporte.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CalculoRequestDTO {
    private BigDecimal distanciaKm;
}