package com.transportes.transporte.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class SolicitudResponseDTO {

    private Long id;
    private String numeroSolicitud;
    private Long clienteId;
    private Long contenedorId;
    private Double origenLat;  // <-- CORREGIDO (de String a Double)
    private Double origenLng;  // <-- CORREGIDO (de String a Double)
    private String origenDir;
    private Double destinoLat; // <-- CORREGIDO (de String a Double)
    private Double destinoLng; // <-- CORREGIDO (de String a Double)
    private String destinoDir;
    private String estado;
    private BigDecimal costoEstimado;
    private Integer tiempoEstimadoMin;
    private BigDecimal costoFinal;
    private Integer tiempoRealMin;
    private OffsetDateTime creadoEn;

    // --- Relación Anidada ---
    // Aquí devolvemos la lista de tramos asociados a esta solicitud
    private List<TramoResponseDTO> tramos;
}