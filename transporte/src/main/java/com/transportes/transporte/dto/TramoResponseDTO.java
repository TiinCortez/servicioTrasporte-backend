package com.transportes.transporte.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class TramoResponseDTO {

    private Long id;
    private Long solicitudId; // Solo el ID, no el objeto Solicitud completo
    private String estado;
    private Long camionId;
    private Long depositoOrigenId;
    private String nombreDepositoOrigen;
    private Long depositoDestinoId;
    private String nombreDepositoDestino;
    private String origenDir;
    private String destinoDir;
    private BigDecimal distanciaKm;
    private Integer duracionMin;
    private BigDecimal costoRealTramo;
    private OffsetDateTime fechaHoraInicio;
    private OffsetDateTime fechaHoraFin;
}