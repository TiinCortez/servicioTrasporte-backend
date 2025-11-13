package com.transportes.transporte.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenCageGeometry {
    private Double lat; // ¡Vienen como números (Double), no Strings!
    private Double lng; // ¡OpenCage usa 'lng', no 'lon'!
}