package com.seguimiento.seguimiento.controllers;

import com.seguimiento.seguimiento.dto.EventoSeguimientoRequestDTO;
import com.seguimiento.seguimiento.dto.EventoSeguimientoResponseDTO;
import com.seguimiento.seguimiento.dto.ResumenSeguimientoDTO;
import com.seguimiento.seguimiento.service.SeguimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class SeguimientoController {

    private final SeguimientoService seguimientoService;

    // GET /api/tracking/solicitudes/{id}
    @GetMapping("/solicitudes/{id}")
    public ResponseEntity<List<EventoSeguimientoResponseDTO>> getLineaTiempo(
            @PathVariable("id") Long solicitudId) {

        List<EventoSeguimientoResponseDTO> eventos =
                seguimientoService.obtenerLineaTiempo(solicitudId);

        return ResponseEntity.ok(eventos);
    }

    // POST /api/tracking/solicitudes/{id}/eventos
    @PostMapping("/solicitudes/{id}/eventos")
    public ResponseEntity<EventoSeguimientoResponseDTO> registrarEvento(
            @PathVariable("id") Long solicitudId,
            @Valid @RequestBody EventoSeguimientoRequestDTO request) {

        EventoSeguimientoResponseDTO creado =
                seguimientoService.registrarEvento(solicitudId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{eventId}")
                .buildAndExpand(creado.getId())
                .toUri();

        return ResponseEntity.created(location).body(creado);
    }

    // 🔹 NUEVO: GET /api/tracking/solicitudes/{id}/resumen
    @GetMapping("/solicitudes/{id}/resumen")
    public ResponseEntity<ResumenSeguimientoDTO> getResumen(
            @PathVariable("id") Long solicitudId) {

        ResumenSeguimientoDTO resumen = seguimientoService.obtenerResumen(solicitudId);

        if (resumen == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(resumen);
    }
}
