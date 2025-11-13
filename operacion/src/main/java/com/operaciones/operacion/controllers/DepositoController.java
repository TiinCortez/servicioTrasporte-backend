package com.operaciones.operacion.controllers;

import com.operaciones.operacion.dto.DepositoDTO;
import com.operaciones.operacion.service.DepositoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/operaciones/depositos") 
public class DepositoController {

    @Autowired
    private DepositoService depositoService;

    // 1. Primer endpoint para obtener un deposito segun su ID.
    @GetMapping("/{id}")
    public ResponseEntity<DepositoDTO> getById(@PathVariable Long id) {
        DepositoDTO dto = depositoService.getDepositoById(id);
        return ResponseEntity.ok(dto);
    }

    // 2. Segundo endpoint para obtener todos los depósitos.
    @GetMapping
    public ResponseEntity<List<DepositoDTO>> getAll() {
        List<DepositoDTO> lista = depositoService.getAllDepositos();
        return ResponseEntity.ok(lista);
    }

    // 3. Endpoint para crear un nuevo depósito
    @PostMapping
    public ResponseEntity<DepositoDTO> createDeposito(@Valid @RequestBody DepositoDTO depositoDto) {
        DepositoDTO creado = depositoService.createDeposito(depositoDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(creado);
    }
}