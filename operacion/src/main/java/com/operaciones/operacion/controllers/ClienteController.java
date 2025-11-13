package com.operaciones.operacion.controllers;

import com.operaciones.operacion.dto.ClienteDTO;
import com.operaciones.operacion.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/operaciones/clientes")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	@GetMapping
	public ResponseEntity<List<ClienteDTO>> getAll() {
		return ResponseEntity.ok(clienteService.getAllClientes());
	}
	// 1. Endpoint para obtener un cliente por su ID.
	@GetMapping("/{id}")
	public ResponseEntity<ClienteDTO> getById(@PathVariable Long id) {
		return ResponseEntity.ok(clienteService.getClienteById(id));
	}

	// 2. Endpoint para crear un nuevo cliente.
	@PostMapping
	public ResponseEntity<ClienteDTO> create(@Valid @RequestBody ClienteDTO dto) {
		ClienteDTO creado = clienteService.createCliente(dto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(creado.getId()).toUri();
		return ResponseEntity.created(location).body(creado);
	}

	// 3. Endpoint para eliminar un cliente por su ID.
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		clienteService.deleteCliente(id);
		return ResponseEntity.noContent().build();
	}

}
