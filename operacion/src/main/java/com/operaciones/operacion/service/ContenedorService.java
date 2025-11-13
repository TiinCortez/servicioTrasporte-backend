package com.operaciones.operacion.service;

import com.operaciones.operacion.dto.ActualizarEstadoDTO;
import com.operaciones.operacion.dto.ContenedorDTO;
import com.operaciones.operacion.entities.Contenedor;
import com.operaciones.operacion.entities.Cliente;
import com.operaciones.operacion.repository.ContenedorRepository;
import com.operaciones.operacion.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContenedorService {

    @Autowired
    private ContenedorRepository contenedorRepository;

    @Autowired
    private ClienteRepository clienteRepository; // <-- Inyectas el repo de Cliente

    public ContenedorDTO getContenedorById(Long id) {
        Contenedor entidad = contenedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contenedor no encontrado con id: " + id));
        return entidadToDto(entidad);
    }

    public List<ContenedorDTO> getAllContenedores() {
        return contenedorRepository.findAll().stream()
                .map(this::entidadToDto)
                .collect(Collectors.toList());
    }

    public List<ContenedorDTO> getContenedoresByClienteId(Long clienteId) {
        return contenedorRepository.findByClienteId(clienteId).stream()
                .map(this::entidadToDto)
                .collect(Collectors.toList());
    }

    public ContenedorDTO createContenedor(ContenedorDTO dto) {
        
        // --- Lógica de Asociación ---
        Cliente clienteAsociado = clienteRepository.findById(dto.getClienteId()) // <-- Usas el clienteId del DTO
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + dto.getClienteId()));

        Contenedor entidad = Contenedor.builder()
                .codigo(dto.getCodigo())
                .capacidadKg(dto.getCapacidadKg())
                .estado(dto.getEstado())
                .cliente(clienteAsociado) // <-- Asignas la entidad Cliente completa
                .build();
        
        Contenedor saved = contenedorRepository.save(entidad);
        return entidadToDto(saved);
    }

    private ContenedorDTO entidadToDto(Contenedor entidad) {
        ContenedorDTO dto = new ContenedorDTO();
        dto.setId(entidad.getId());
        dto.setCodigo(entidad.getCodigo());
        dto.setCapacidadKg(entidad.getCapacidadKg());
        dto.setEstado(entidad.getEstado());
        dto.setCreadoEn(entidad.getCreadoEn());
        
        if (entidad.getCliente() != null) {
            dto.setClienteId(entidad.getCliente().getId()); // <-- Devuelves el ID del cliente
        }
        
        return dto;
    }

    // Actualiza sólo el estado del contenedor y devuelve el DTO actualizado
    public ContenedorDTO updateContenedorEstado(Long id, String nuevoEstado) {
        Contenedor entidad = contenedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contenedor no encontrado con id: " + id));

        entidad.setEstado(nuevoEstado);
        Contenedor saved = contenedorRepository.save(entidad);
        return entidadToDto(saved);
    }
    @Transactional
    public ContenedorDTO actualizarEstado(Long id, ActualizarEstadoDTO dto) {
        // 1. Busca el contenedor
        Contenedor entidad = contenedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contenedor no encontrado con id: " + id));
        
        // 2. Actualiza el estado
        entidad.setEstado(dto.getEstado());
        
        // 3. Guarda los cambios
        Contenedor saved = contenedorRepository.save(entidad);
        
        // 4. Devuelve el DTO actualizado
        return entidadToDto(saved); // (Reutilizamos tu método de mapeo)
    }
}