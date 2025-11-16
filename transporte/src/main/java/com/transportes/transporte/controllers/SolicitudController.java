package com.transportes.transporte.controllers;

import com.transportes.transporte.dto.AsignarCamionRequestDTO;
import com.transportes.transporte.dto.SolicitudRequestDTO;
import com.transportes.transporte.dto.SolicitudResponseDTO;
import com.transportes.transporte.dto.TramoAsignadoResponseDTO;
import com.transportes.transporte.entities.Tramo;
import com.transportes.transporte.service.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/transporte") // URL base para este microservicio
public class SolicitudController {

    @Autowired
    private SolicitudService solicitudService;

    // 1. Primer endpoint para crear una nueva solicituda
    @PostMapping("/solicitudes")
    public ResponseEntity<SolicitudResponseDTO> create(
            @Valid @RequestBody SolicitudRequestDTO requestDTO) {
        
        SolicitudResponseDTO response = solicitudService.createSolicitud(requestDTO);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.getId()).toUri();

        return ResponseEntity.created(location).body(response);
    }
    
    // 2. Segundo endpoint para obtener una solicitud según su ID.
    @GetMapping("/solicitudes/{id}")
    public ResponseEntity<SolicitudResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.getSolicitudById(id));
    }
    // 3. Tercer endpoint para obtener todas las solicitudes.
    @GetMapping("/solicitudes")
    public ResponseEntity<List<SolicitudResponseDTO>> getAll() {
        return ResponseEntity.ok(solicitudService.getAllSolicitudes());
    }

    @PostMapping("/solicitudes/{id}/asignar-camion")
    public ResponseEntity<List<TramoAsignadoResponseDTO>> asignarCamionASolicitud(
            @PathVariable Long id,
            @Valid @RequestBody AsignarCamionRequestDTO request) {

        List<Tramo> tramosActualizados =
                solicitudService.asignarCamionASolicitud(id, request.getCamionId());

        List<TramoAsignadoResponseDTO> response = tramosActualizados.stream()
                .map(t -> new TramoAsignadoResponseDTO(
                        t.getId(),
                        t.getEstado(),
                        t.getCamionId()
                ))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/solicitudes/{id}/finalizar")
    public ResponseEntity<SolicitudResponseDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.finalizarSolicitud(id));
    }







}