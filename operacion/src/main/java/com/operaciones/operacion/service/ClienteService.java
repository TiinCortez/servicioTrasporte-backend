package com.operaciones.operacion.service;

import com.operaciones.operacion.dto.ClienteDTO;
import com.operaciones.operacion.entities.Cliente;
import com.operaciones.operacion.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<ClienteDTO> getAllClientes() {
        return clienteRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public ClienteDTO getClienteById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + id));
        return entityToDto(cliente);
    }

    public ClienteDTO createCliente(ClienteDTO dto) {
        Cliente entidad = dtoToEntity(dto);
        Cliente saved = clienteRepository.save(entidad);
        return entityToDto(saved);
    }

    public void deleteCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private ClienteDTO entityToDto(Cliente entidad) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(entidad.getId());
        dto.setRazonSocial(entidad.getRazonSocial());
        dto.setCuit(entidad.getCuit());
        dto.setDireccion(entidad.getDireccion());
        dto.setTelefono(entidad.getTelefono());
        dto.setEmail(entidad.getEmail());
        return dto;
    }

    private Cliente dtoToEntity(ClienteDTO dto) {
        Cliente entidad = Cliente.builder()
                .id(dto.getId())
                .razonSocial(dto.getRazonSocial())
                .cuit(dto.getCuit())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .build();
        return entidad;
    }
}
