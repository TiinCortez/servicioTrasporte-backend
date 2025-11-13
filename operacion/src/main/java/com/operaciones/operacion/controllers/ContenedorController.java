package com.operaciones.operacion.controllers;

import com.operaciones.operacion.dto.ActualizarEstadoDTO;
import com.operaciones.operacion.dto.ContenedorDTO;
import com.operaciones.operacion.service.ContenedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/operaciones/contenedores")
public class ContenedorController {

    @Autowired
    private ContenedorService contenedorService;
    
    // 1. Endpoint para obtener un contenedor por su ID.
    @GetMapping("/{id}")
    public ResponseEntity<ContenedorDTO> getById(@PathVariable Long id) {
        ContenedorDTO dto = contenedorService.getContenedorById(id);
        return ResponseEntity.ok(dto);
    }
    // 2. Endpoint para obtener todos los contenedores.
    @GetMapping
    public ResponseEntity<List<ContenedorDTO>> getAll() {
        return ResponseEntity.ok(contenedorService.getAllContenedores());
    }

    // 3. Endpoint para obtener contenedores para un cliente específico
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ContenedorDTO>> getByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(contenedorService.getContenedoresByClienteId(clienteId));
    }

    // 4. Endpoint para crear un nuevo contenedor.
    @PostMapping
    public ResponseEntity<ContenedorDTO> create(@Valid @RequestBody ContenedorDTO dto) {
        ContenedorDTO creado = contenedorService.createContenedor(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    // 5. Endpoint para actualizar el estado de un contenedor.
    @PutMapping("/{id}/estado")
    public ResponseEntity<ContenedorDTO> actualizarEstado(
            @PathVariable Long id, 
            @Valid @RequestBody ActualizarEstadoDTO dto) {

        System.out.println("➡️ Llego estado = " + dto.getEstado());

        ContenedorDTO actualizado = contenedorService.actualizarEstado(id, dto);
        return ResponseEntity.ok(actualizado);
    }

}
