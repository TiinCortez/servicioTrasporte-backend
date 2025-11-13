package com.transportes.transporte.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora todo lo que no necesitamos
public class OpenCageResponse {
    private List<OpenCageResult> results;
}