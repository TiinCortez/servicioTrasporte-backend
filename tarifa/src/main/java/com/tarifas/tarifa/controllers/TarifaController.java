package com.tarifas.tarifa.controllers;

import com.tarifas.tarifa.dto.CalculoRequestDTO;
import com.tarifas.tarifa.dto.CalculoResponseDTO;
import com.tarifas.tarifa.dto.TarifaBaseDTO;
import com.tarifas.tarifa.service.TarifaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/tarifas")
public class TarifaController {

    @Autowired
    private TarifaService tarifaService;

    /**
     * ¡EL ENDPOINT CLAVE!
     * Este es el que llamará 'ms-transporte'.
     */
    @PostMapping("/calcular")
    public ResponseEntity<CalculoResponseDTO> calcular(@Valid @RequestBody CalculoRequestDTO request) {
        CalculoResponseDTO response = tarifaService.calcularCosto(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de gestión (para crear tarifas)
     */
    @PostMapping("/base")
    public ResponseEntity<TarifaBaseDTO> crearTarifaBase(@Valid @RequestBody TarifaBaseDTO dto) {
        TarifaBaseDTO creada = tarifaService.createTarifaBase(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(creada.getId()).toUri();
        return ResponseEntity.created(location).body(creada);
    }
    
    // (Aquí irían los otros endpoints GET, PUT, DELETE para gestionar tarifas y recargos)
}