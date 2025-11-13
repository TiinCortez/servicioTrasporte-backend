package com.transportes.transporte.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

// El JSON principal de OSRM tiene un campo llamado "routes"
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OsrmResponse {
    private List<OsrmRoute> routes;
}