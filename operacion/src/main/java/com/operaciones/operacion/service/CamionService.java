package com.operaciones.operacion.service;

import com.operaciones.operacion.dto.CamionDTO;
import com.operaciones.operacion.entities.Camion;
import com.operaciones.operacion.repository.CamionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CamionService {

    @Autowired
    private CamionRepository camionRepository;

    // Método para buscar un Camion por ID y devolver su DTO
    public CamionDTO findById(Long id) {
        Camion camionEntidad = camionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Camión no encontrado con id: " + id));
        return convertiEntidadADTO(camionEntidad);
    }

    // Obtener todos los camiones y devolver 
    public List<CamionDTO> getAllCamiones() {
        return camionRepository.findAll().stream()
                .map(this::convertiEntidadADTO)
                .collect(Collectors.toList());
    }
    
    // Método para obtener camiones filtrando por disponibilidad
    public List<CamionDTO> getCamionesByDisponibilidad(Boolean disponible) {
        return camionRepository.findByDisponible(disponible).stream()
                .map(this::convertiEntidadADTO)
                .collect(Collectors.toList());
    }

    // Mapeador privado para la conversión
    private CamionDTO convertiEntidadADTO(Camion entidad) {
        CamionDTO dto = new CamionDTO();
        dto.setId(entidad.getId());
        dto.setDominio(entidad.getDominio());
        dto.setNombreTransportista(entidad.getNombreTransportista());
        dto.setTelefono(entidad.getTelefono());
        dto.setCapPesoKg(entidad.getCapPesoKg());
        dto.setCapVolM3(entidad.getCapVolM3());
        dto.setCostoBaseKm(entidad.getCostoBaseKm());
        dto.setConsumo100km(entidad.getConsumo100km());
        dto.setDisponible(entidad.getDisponible());
        return dto;
    }

    // Mapea DTO a Entidad para guardar
    private Camion convertiDTOAEntidad(CamionDTO dto) {
        Camion entidad = new Camion();
        entidad.setDominio(dto.getDominio());
        entidad.setNombreTransportista(dto.getNombreTransportista());
        entidad.setTelefono(dto.getTelefono());
        entidad.setCapPesoKg(dto.getCapPesoKg());
        entidad.setCapVolM3(dto.getCapVolM3());
        entidad.setCostoBaseKm(dto.getCostoBaseKm());
        entidad.setConsumo100km(dto.getConsumo100km());
        // si viene null, dejamos por defecto true
        entidad.setDisponible(dto.getDisponible() == null ? true : dto.getDisponible());
        return entidad;
    }

    // Método para crear un nuevo Camión a partir del DTO
    public CamionDTO createCamion(CamionDTO dto) {
        Camion entidad = convertiDTOAEntidad(dto);
        Camion saved = camionRepository.save(entidad);
        return convertiEntidadADTO(saved);
    }
}