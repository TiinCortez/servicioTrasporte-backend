package com.operaciones.operacion.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data // Lombok para @Getter, @Setter, etc.
public class CamionDTO {
    
    // Solo los datos que queremos exponer
    private Long id;
    private String dominio;
    private String nombreTransportista;
    private String telefono;
    private BigDecimal capPesoKg;
    private BigDecimal capVolM3;
    private BigDecimal costoBaseKm;
    private BigDecimal consumo100km;
    private Boolean disponible;
}