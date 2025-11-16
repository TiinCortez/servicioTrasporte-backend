package com.transportes.transporte.dto;

import com.transportes.transporte.entities.Tramo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
@NoArgsConstructor

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

    public TramoResponseDTO(Tramo t) {
        this.id = t.getId();
        this.estado = t.getEstado();
        this.camionId = t.getCamionId();
        this.fechaHoraInicio = t.getFechaHoraInicio();
        this.fechaHoraFin = t.getFechaHoraFin();
    }

}

