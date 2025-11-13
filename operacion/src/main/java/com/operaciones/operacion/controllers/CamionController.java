package com.operaciones.operacion.controllers;

import com.operaciones.operacion.dto.CamionDTO;
import com.operaciones.operacion.service.CamionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
// ¡Esta URL coincide con tu PDF!
@RequestMapping("/api/operaciones/camiones") 
public class CamionController {

    @Autowired
    private CamionService camionService;
    
    // 1. Primer endpoint para obtener un camion segun su ID.
    @GetMapping("/{id}")
    public ResponseEntity<CamionDTO> getCamionById(@PathVariable Long id) {
        CamionDTO dto = camionService.findById(id);
        return ResponseEntity.ok(dto);
    }

    // 2. Segundo endpoint para obtener todos los camiones.
    @GetMapping
    public ResponseEntity<List<CamionDTO>> getAllCamiones(@RequestParam(name = "disponible", required = false) Boolean disponible) {
        List<CamionDTO> lista;
        if (disponible == null) {
            lista = camionService.getAllCamiones();
        } else {
            lista = camionService.getCamionesByDisponibilidad(disponible);
        }
        return ResponseEntity.ok(lista);
    }

    // 3. Tercer endpoint para crear un nuevo camión nuevo con POST.
    @PostMapping
    public ResponseEntity<CamionDTO> createCamion(@Valid @RequestBody CamionDTO camionDto) {
        CamionDTO creado = camionService.createCamion(camionDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(creado);
    }
}