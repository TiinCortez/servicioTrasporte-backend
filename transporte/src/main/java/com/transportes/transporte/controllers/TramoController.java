package com.transportes.transporte.controllers;

import com.transportes.transporte.dto.AsignarCamionRequestDTO;
import com.transportes.transporte.dto.TramoAsignadoResponseDTO;
import com.transportes.transporte.dto.TramoResponseDTO;
import com.transportes.transporte.entities.Tramo;
import com.transportes.transporte.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transporte")
@RequiredArgsConstructor
public class TramoController {

    private final SolicitudService solicitudService;

    @PostMapping("/tramos/{tramoId}/asignar-camion")
    public ResponseEntity<TramoAsignadoResponseDTO> asignarCamion(
            @PathVariable Long tramoId,
            @Valid @RequestBody AsignarCamionRequestDTO request) {

        Tramo tramo = solicitudService.asignarCamionATramo(tramoId, request.getCamionId());

        TramoAsignadoResponseDTO response = new TramoAsignadoResponseDTO(
                tramo.getId(),
                tramo.getEstado(),
                tramo.getCamionId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/tramos/{id}/iniciar")
    public ResponseEntity<TramoResponseDTO> iniciarTramo(@PathVariable Long id) {
        Tramo t = solicitudService.iniciarTramo(id);
        return ResponseEntity.ok(new TramoResponseDTO(t));
    }

    @PostMapping("/tramos/{id}/finalizar")
    public ResponseEntity<TramoResponseDTO> finalizarTramo(@PathVariable Long id) {
        Tramo t = solicitudService.finalizarTramo(id);
        return ResponseEntity.ok(new TramoResponseDTO(t));
    }




}
