package com.transportes.transporte.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

//Datos de un depósito (ubicado en nuestro ms de transporte)
//Ignorando algunos datos y trayendo solo los importantes
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositoDTO {
    private Long id;
    private String nombre;
    private Double lat;
    private Double lng;
    private BigDecimal costoEstadiaDiario;
}