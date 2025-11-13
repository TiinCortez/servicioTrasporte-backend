package com.transportes.transporte.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenCageResult {
    private OpenCageGeometry geometry; // Un objeto anidado
}