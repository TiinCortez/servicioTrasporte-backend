package com.transportes.transporte.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

// Cada "route" tiene la distancia y duración que queremos
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OsrmRoute {
    private Double distance; // en metros
    private Double duration; // en segundos
}